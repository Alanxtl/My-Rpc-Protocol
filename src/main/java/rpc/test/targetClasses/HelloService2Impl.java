package rpc.test.targetClasses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloService2Impl implements HelloService {
    
    @Override
    public String sayHello(String e) {
        log.info("HelloService2Impl收到: {}.", e);
        String result = "Hello description is " + e;
        log.info("HelloService2Impl返回: {}.", result);
        return result;
    }
}
