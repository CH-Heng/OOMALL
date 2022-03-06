package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.ProductRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class ProductFactory implements Factory<ProductRetVo> {

    @Override
    public ProductRetVo create(Long id) {
        ProductRetVo retVo = new ProductRetVo();

        retVo.setId(id);
        retVo.setName("沙壁");
        retVo.setCategory(new CategoryFactory().create(666L));

        return retVo;
    }
}
