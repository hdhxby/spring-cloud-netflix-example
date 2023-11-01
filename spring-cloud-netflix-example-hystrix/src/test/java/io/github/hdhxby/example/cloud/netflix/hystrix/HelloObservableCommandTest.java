package io.github.hdhxby.example.cloud.netflix.hystrix;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.ExecutionException;

public class HelloObservableCommandTest {


    private static final Logger log = LoggerFactory.getLogger(HelloCommand.class);
    /**
     *
     * 集合
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void observe() throws ExecutionException, InterruptedException {
        HelloObservableCommand helloCommand = new HelloObservableCommand();
        Observable<String> observable = helloCommand.observe();
        log.debug("observe");
        observable.toBlocking()
                .forEach(o -> log.debug(o));
        observable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(String o) {
                log.debug(o);
            }
        });
    }


    /**
     *
     * 集合
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void toObservable() throws ExecutionException, InterruptedException {
        HelloObservableCommand helloCommand = new HelloObservableCommand();
        Observable<String> observable = helloCommand.toObservable();
        log.debug("toObservable");
        observable.toBlocking()
                .forEach(o -> log.debug(o));
        observable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(String o) {
                log.debug(o);
            }
        });
    }
}
