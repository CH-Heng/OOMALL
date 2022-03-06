package cn.edu.xmu.oomall.payment.microservice;

import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayRefundVo;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayTransactionVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/8 11:07
 **/
@Component
@FeignClient(value = "wechatpay-service")
public interface WechatPayService {
    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    public InternalReturnObject createTransaction(@Validated @RequestBody WeChatPayTransactionVo weChatPayTransactionVo);

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    public InternalReturnObject getTransaction(@PathVariable("out_trade_no") String outTradeNo);

    @PostMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}/close")
    public InternalReturnObject closeTransaction(@PathVariable("out_trade_no") String outTradeNo);

    @PostMapping("/internal/wechat/refund/domestic/refunds")
    public InternalReturnObject createRefund(@Validated @RequestBody WeChatPayRefundVo weChatPayRefundVo);

    @GetMapping("/internal/wechat/refund/domestic/refunds/{out_refund_no}")
    public InternalReturnObject getRefund(@PathVariable("out_refund_no") String outRefundNo);

    @GetMapping("/internal/wechat/bill/fundflowbill")
    public InternalReturnObject getFundFlowBill(@RequestParam("bill_date") String billDate);
}
