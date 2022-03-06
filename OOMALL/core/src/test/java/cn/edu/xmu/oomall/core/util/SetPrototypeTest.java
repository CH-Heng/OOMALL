package cn.edu.xmu.oomall.core.util;

import cn.edu.xmu.oomall.shop.model.po.CouponActivityPo;
import cn.edu.xmu.oomall.shop.model.po.Nonepo;
import org.junit.jupiter.api.Test;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @date： 2021/11/12 11:12
 * @version: 1.0
 * @author: Wangzixia 32420182202938
 */
public class SetPrototypeTest {


    @Test
    public void test() {
        CouponActivityPo po = new CouponActivityPo();
        boolean createFlag = setPoCreatedFields(po, 1L, "zhangsan");
        assertEquals(true,createFlag);
        boolean createFlag2 = setPoCreatedFields(po, 3L, "zhangsan");
        assertEquals(true,createFlag2);
        boolean createFlag3 = setPoCreatedFields(po, 3L, "zhaosi");
        assertEquals(true,createFlag3);
        Nonepo po2=new Nonepo();
        boolean createFlag4 = setPoCreatedFields(po2, 3L, "zhaosi");
        assertEquals(false,createFlag4);

        boolean modifyFlag = setPoModifiedFields(po, 2L, "lisi");
        assertEquals(true,modifyFlag);
        boolean modifyFlag2 = setPoModifiedFields(po, 3L, "lisi");
        assertEquals(true,modifyFlag2);
        boolean modifyFlag3 = setPoCreatedFields(po, 3L, "zhaosi");
        assertEquals(true,modifyFlag3);
        boolean modifyFlag4 = setPoCreatedFields(po2, 3L, "zhaosi");
        assertEquals(false,modifyFlag4);


    }
}

