package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.StrategyRetVo;
import cn.edu.xmu.oomall.liquidation.microservice.vo.SuccessfulShareRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;
import cn.edu.xmu.oomall.liquidation.util.base.ListFactory;

public class SuccessfulShareFactory implements Factory<SuccessfulShareRetVo> {

    @Override
    public SuccessfulShareRetVo create(Long id) {
        SuccessfulShareRetVo retVo = new SuccessfulShareRetVo();

        retVo.setId(id);
        retVo.setStrategy(new ListFactory(new StrategyFactory()).create(5));
        retVo.setSharerId(666L);

        return retVo;
    }
}
