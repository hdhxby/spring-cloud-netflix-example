package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CacheCommandTest {

    @Test
    public void test() {
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();
        try {
            CacheCommand hystrixRequestContext1 = new CacheCommand("value");
            CacheCommand hystrixRequestContext2 = new CacheCommand("value");
            // 第一次执行, 不走缓存
            hystrixRequestContext1.execute();
            assertFalse(hystrixRequestContext1.isResponseFromCache());
            // 走缓存
            hystrixRequestContext2.execute();
            assertTrue(hystrixRequestContext2.isResponseFromCache());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hystrixRequestContext.shutdown();
        }
        CacheCommand hystrixRequestContext3 = new CacheCommand("value");
        hystrixRequestContext3.execute();
        assertFalse(hystrixRequestContext3.isResponseFromCache());
    }


    @Test
    public void clear() {
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();
        try {
            CacheCommand hystrixRequestContext1 = new CacheCommand("value");
            CacheCommand hystrixRequestContext2 = new CacheCommand("value");
            // 第一次执行, 不走缓存
            hystrixRequestContext1.execute();
            assertFalse(hystrixRequestContext1.isResponseFromCache());
            // 走缓存
            hystrixRequestContext2.execute();
            assertTrue(hystrixRequestContext2.isResponseFromCache());

            HystrixRequestCache.getInstance(HystrixCommandKey.Factory.asKey("cache"),
                    HystrixConcurrencyStrategyDefault.getInstance())
                    .clear("value");
            // 清空缓存
            CacheCommand hystrixRequestContext3 = new CacheCommand("value");
            // 不走缓存
            hystrixRequestContext3.execute();
            assertFalse(hystrixRequestContext3.isResponseFromCache());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hystrixRequestContext.shutdown();
        }
    }

}
