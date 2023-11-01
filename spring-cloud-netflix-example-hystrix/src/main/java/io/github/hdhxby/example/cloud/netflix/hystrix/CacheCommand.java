package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class CacheCommand extends HystrixCommand<String> {
    String value;
    public CacheCommand(String value) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("cacheCommandGroup")));
        this.value = value;
    }

    @Override
    protected String run() throws Exception {
        return value;
    }

    @Override
    protected String getCacheKey() {
        return value;
    }
}
