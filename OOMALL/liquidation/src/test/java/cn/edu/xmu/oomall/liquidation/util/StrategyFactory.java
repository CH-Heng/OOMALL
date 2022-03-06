package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.StrategyRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class StrategyFactory implements Factory<StrategyRetVo> {

    @Override
    public StrategyRetVo create(Long id) {
        StrategyRetVo retVo = new StrategyRetVo();

        retVo.setQuantity(id);
        retVo.setPercentage((int) (10 * id));

        return retVo;
    }
}
