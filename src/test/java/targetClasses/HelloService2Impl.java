package targetClasses;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloService2Impl implements HelloService {
    
    @Override
    public String sayHello(String e) {
        System.out.printf("HelloService2Impl收到: {}. \n", e);
        String result = "Hello description is " + e;
        System.out.printf("HelloService2Impl返回: {}. \n", result);
        return result;
    }
}
