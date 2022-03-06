package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.CategoryRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class CategoryFactory implements Factory<CategoryRetVo> {

    @Override
    public CategoryRetVo create(Long id) {
        CategoryRetVo retVo = new CategoryRetVo();

        retVo.setId(id);
        retVo.setName("沙壁");
        retVo.setCommissionRatio(10);

        return retVo;
    }
}
