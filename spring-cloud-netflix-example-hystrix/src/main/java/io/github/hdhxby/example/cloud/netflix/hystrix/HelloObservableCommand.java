package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 获取多条数据
 */
public class HelloObservableCommand extends HystrixObservableCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(HelloCommand.class);

    private RestTemplate restTemplate = new RestTemplate();

    public HelloObservableCommand(){
        this("helloObservableCommandGroup");
    }

    public HelloObservableCommand(String key){
        super(HystrixCommandGroupKey.Factory.asKey(key));
    }

    public HelloObservableCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected Observable<String> construct() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                log.debug("call");
                 subscriber.onNext("hello");
                 subscriber.onNext("world");
                 subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

}
