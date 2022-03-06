package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.OrderItemRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class OrderItemFactory implements Factory<OrderItemRetVo> {

    @Override
    public OrderItemRetVo create(Long id) {
        OrderItemRetVo retVo = new OrderItemRetVo();

        retVo.setId(id);
        retVo.setShopId(1L);
        retVo.setOrderId(666L);
        retVo.setProductId(666L);
        retVo.setPrice(100L);
        retVo.setDiscountPrice(50L);
        retVo.setQuantity(5);
        retVo.setOnsaleId(666L);
        retVo.setCustomerId(666L);

        return retVo;
    }
}
