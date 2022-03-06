package cn.edu.xmu.oomall.aftersale.microservice;

import cn.edu.xmu.oomall.aftersale.microservice.vo.SimplePaymentVo;
import cn.edu.xmu.oomall.aftersale.model.vo.PaymentVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleRefundVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "payment-service")
public interface PaymentService {
    /**
     * 通过订单id查支付信息
     * @author wxt
     */
    @GetMapping("orders/{id}/payment")
    ReturnObject<SimplePaymentVo> getPayment(@PathVariable Long id);

    /**
     * 内部API-通过售后单序号查询售后单的支付信息
     * @author wxt
     */
    @GetMapping("internal/payment/aftersale")
    InternalReturnObject<SimplePaymentVo> getPaymentBySn(@RequestParam(value = "sn") String sn);

    /**内部API-通过顾客id给顾客退款和退积点
     * @author 张晖婧
     */
    @PostMapping("/internal/shops/{shopId}refund/{id}")
    InternalReturnObject refundForAftersale(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id,
                                          @Validated @RequestBody AftersaleRefundVo aftersaleRefundVo);
    /**内部API-建立顾客支付对象
     * @author 张晖婧
     */
    @PostMapping("/internal/payments")
    InternalReturnObject setCustomerPayment(@RequestParam("shopId") Long shopId,
                                            @RequestParam("id") Long id,
                                            @Validated @RequestBody PaymentVo paymentVo);
}
