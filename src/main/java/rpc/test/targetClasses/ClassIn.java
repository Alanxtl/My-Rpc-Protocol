package rpc.test.targetClasses;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClassIn implements Serializable {
    public Integer in = 123;

}
