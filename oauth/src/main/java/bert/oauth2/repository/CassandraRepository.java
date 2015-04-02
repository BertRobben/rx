package bert.oauth2.repository;

import java.util.Optional;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import bert.oauth2.util.AsyncHelper;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class CassandraRepository<T> implements Repository<T> {

	private static final String KEY = "key";

	private static final String VALUE = "value";

	private final Session session;

	private final PreparedStatement select;
	private final PreparedStatement insert;

	private final Marshaller<T> marshaller;

	private final PreparedStatement selectAll;

	public CassandraRepository(String tableName, Marshaller<T> marshaller, Session session) {
		this.marshaller = marshaller;
		this.session = session;
		checkTable(tableName);
		select = session.prepare(QueryBuilder.select().all().from(tableName).where().and(QueryBuilder.eq(KEY, "?"))
		    .getQueryString());
		insert = session.prepare("insert into " + tableName + "(" + KEY + ", " + VALUE + ") values (?, ?) using ttl ?");
		selectAll = session.prepare(QueryBuilder.select().all().from(tableName).getQueryString());
	}

	private void checkTable(String tableName) {
		if (session.getCluster().getMetadata().getKeyspace(session.getLoggedKeyspace()).getTable(tableName) == null) {
			session.execute("CREATE TABLE " + tableName + "(" + KEY + " text PRIMARY KEY, " + VALUE + " text)");
		}
	}

	@Override
	public Observable<T> store(String key, T value, Integer ttl) {
		return AsyncHelper.fromFuture(session.executeAsync(insert.bind(key, marshaller.toString(value), ttl))).map(
		    resultSet -> value);
	}

	@Override
	public Observable<Optional<T>> retrieve(String key) {
		return AsyncHelper.fromFuture(session.executeAsync(select.bind(key))).map(
				resultSet -> Optional.ofNullable(resultSet.one()).map(this::fromRow));
	}

	@Override
	public Observable<T> retrieveAll() {
		Observable<ResultSet> rs = AsyncHelper.fromFuture(session.executeAsync(selectAll.bind()));
		return rs.flatMap(resultSet -> Observable.create((OnSubscribe<T>) subscriber -> process(subscriber, resultSet)));
	}

	private void process(Subscriber<? super T> subscriber, ResultSet resultSet) {
		while (resultSet.getAvailableWithoutFetching() > 0) {
			subscriber.onNext(fromRow(resultSet.one()));
		}
		if (!resultSet.isFullyFetched()) {
			Futures.addCallback(resultSet.fetchMoreResults(), new FutureCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					process(subscriber, resultSet);
				}

				@Override
				public void onFailure(Throwable t) {
					subscriber.onError(t);
				}
			});
		} else {
			subscriber.onCompleted();
		}
	}

	private T fromRow(Row row) {
		return marshaller.fromString(row.getString(VALUE));
	}

}
