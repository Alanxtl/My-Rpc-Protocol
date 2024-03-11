package rpc.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestServiceImpl implements TestService {

    @Override
    public String hello() {
        return "hello";
    }
}
