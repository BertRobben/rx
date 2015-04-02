package bert.oauth2.util;

import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class AsyncHelper {

	public static <T> Observable<T> fromFuture(ListenableFuture<T> future) {
		return Observable.create(new OnSubscribe<T>() {

			@Override
			public void call(Subscriber<? super T> subscriber) {
				Futures.addCallback(future, new FutureCallback<T>() {

					@Override
					public void onSuccess(T result) {
						subscriber.onNext(result);
						subscriber.onCompleted();
					}

					@Override
					public void onFailure(Throwable t) {
						subscriber.onError(t);
					}
				});
			}
		});

	}

	public static <T> DeferredResult<T> asDeferredResult(Observable<T> observable) {
		DeferredResult<T> result = new DeferredResult<>();
		observable.subscribe(t -> result.setResult(t), ex -> result.setErrorResult(ex));
		return result;
	}

}
