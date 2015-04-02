package bert.oauth2.repository;

import java.util.Optional;

import rx.Observable;

public interface Repository<T> {

	Observable<T> store(String key, T accessToken, Integer ttl);

	Observable<Optional<T>> retrieve(String token);

	Observable<T> retrieveAll();

}
