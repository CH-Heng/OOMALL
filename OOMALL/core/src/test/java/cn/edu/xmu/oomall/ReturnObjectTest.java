package cn.edu.xmu.oomall;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.junit.jupiter.api.Test;

/**
 * @Author GXC
 */
public class ReturnObjectTest {
    @Test
    public void test1(){
        InternalReturnObject internalReturnObject=new InternalReturnObject();
        internalReturnObject.setData("Faker!What was that!");
        internalReturnObject.setErrno(500);
        internalReturnObject.setErrmsg("Faker!What was that!");
        ReturnObject returnObject=new ReturnObject(internalReturnObject);
        assert returnObject.getCode().equals(ReturnNo.INTERNAL_SERVER_ERR);
        assert returnObject.getErrmsg().equals("Faker!What was that!");
        assert returnObject.getData().equals("Faker!What was that!");
    }
}
