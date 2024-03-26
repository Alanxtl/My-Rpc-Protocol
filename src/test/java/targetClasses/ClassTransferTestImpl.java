package targetClasses;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassTransferTestImpl implements ClassTransferTest {

    @Override
    public ClassOut testTransferClass(ClassIn in) {
        System.out.printf("ClassTransferTestImpl收到: {}. \n", in.toString());
        ClassOut out = new ClassOut();
        out.setOut(in.getIn());
        System.out.printf("ClassTransferTestImpl返回: {}. \n", out);
        return out;
    }
}
