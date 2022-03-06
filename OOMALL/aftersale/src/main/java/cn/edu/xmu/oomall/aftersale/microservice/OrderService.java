package cn.edu.xmu.oomall.aftersale.microservice;

import cn.edu.xmu.oomall.aftersale.model.vo.OrderInfoForExchangeVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "order-service")
public interface OrderService {
    /**内部API-管理员建立售后换货订单
     * @author 张晖婧
     */
    @PutMapping("/internal/shops/{shopId}/orders")
    InternalReturnObject submitExchangeOrderForAftersale(@PathVariable("shopId") Long shopId,
                                                 @Validated @RequestBody OrderInfoForExchangeVo orderInfoForExchangeVo);

    /**内部API-通过订单明细id查询订单明细以及订单相关信息
     * @author 张晖婧
     */
    @GetMapping("/internal/orderitems/{id}")
    InternalReturnObject getOrderItemForAftersaleByOrderItemId(@PathVariable("id") Long id);

    /** 通过订单id获取订单
     * @author 张晖婧
     */
    @GetMapping("/orders/{id}")
    ReturnObject getOrderById(@PathVariable("id") Long id);
}
