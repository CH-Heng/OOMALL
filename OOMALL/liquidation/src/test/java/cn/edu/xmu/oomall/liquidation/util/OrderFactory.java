package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.OrderRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class OrderFactory implements Factory<OrderRetVo> {

    @Override
    public OrderRetVo create(Long id) {
        OrderRetVo retVo = new OrderRetVo();

        retVo.setId(id);
        retVo.setState(201);
        retVo.setExpressFee(100L);

        return retVo;
    }
}
