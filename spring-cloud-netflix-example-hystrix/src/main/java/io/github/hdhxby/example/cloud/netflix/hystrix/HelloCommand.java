package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取一条数据
 */
//@Component
public class HelloCommand extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(HelloCommand.class);

    public HelloCommand() {
        this("helloCommandGroup");
    }

    public HelloCommand(String key){
        super(HystrixCommandGroupKey.Factory.asKey(key));
    }

    public HelloCommand(Setter setter) {
        super(setter);
    }

    /**
     * 降级
     * @return
     * @throws Exception
     */
    @Override
    protected String run() throws Exception {
        log.debug("run");
        return "hello";
    }

}
