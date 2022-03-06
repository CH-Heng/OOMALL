package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.ShopRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class ShopFactory implements Factory<ShopRetVo> {


    @Override
    public ShopRetVo create(Long id) {
        ShopRetVo retVo = new ShopRetVo();

        retVo.setId(id);
        retVo.setName("沙壁");

        return retVo;
    }
}
