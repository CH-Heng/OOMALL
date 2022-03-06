package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.liquidation.microservice.vo.CustomerPointVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Component
@FeignClient(name = "order-service")
public interface OrderService {

    /**
     * 内部API
     * 根据 orderSn 获得订单
     * @return OrderRetVo
     */
    @GetMapping("/internal/order")
    InternalReturnObject getOrderByOrderSn(@RequestParam String orderSn);

    /**
     * 内部API
     * 获得订单明细
     * @return OrderItemRetVo
     */
    @GetMapping("/internal/orderitem/{id}")
    InternalReturnObject getOrderItem(@PathVariable Long id);

    /**
     * 内部API
     * 根据 OrderId 获得订单明细
     * @return OrderItemRetVo
     */
    @GetMapping("/internal/orderitem")
    InternalReturnObject getOrderItemByOrderId(@RequestParam Long orderId,
                                               @RequestParam Integer page,
                                               @RequestParam Integer pageSize);

}
