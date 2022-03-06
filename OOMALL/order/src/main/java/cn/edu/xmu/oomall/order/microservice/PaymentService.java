package cn.edu.xmu.oomall.order.microservice;


import cn.edu.xmu.oomall.order.microservice.vo.PaymentSimpleRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundSimpleRetVo;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/9
 */
@Component
@FeignClient(value = "payment-service")
public interface PaymentService {
    /**
     * 通过订单编号获得支付单
     * @param documentId 订单编号
     * @return 查询结果
     */
    @GetMapping(value = "/internal/payments")
    InternalReturnObject<List<PaymentSimpleRetVo>> getPaymentsByOrderSn(@RequestParam("documentId") String documentId);

    /**
     * 内部退款api
     * @param refundVo 退款所需参数
     * @return
     */
    @Audit
    @PostMapping("/internal/refund")
    InternalReturnObject refund(@RequestBody RefundVo refundVo);

    /**
     * 通过订单编号获得退款单
     * @param documentId 订单编号
     * @return 查询结果
     */
    @GetMapping(value = "/internal/refunds")
    InternalReturnObject<List<RefundSimpleRetVo>> getRefundsByOrderSn(@RequestParam("documentId") String documentId);



}


