package rpc.test.targetClasses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassTransferTestImpl implements ClassTransferTest {

    @Override
    public ClassOut testTransferClass(ClassIn in) {
        log.info("ClassTransferTestImpl收到: {}.", in.toString());
        ClassOut out = new ClassOut();
        out.setOut(in.getIn());
        log.info("ClassTransferTestImpl返回: {}.", out);
        return out;
    }
}
