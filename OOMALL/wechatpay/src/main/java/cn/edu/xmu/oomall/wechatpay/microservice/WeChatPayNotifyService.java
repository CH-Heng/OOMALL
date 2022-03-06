package cn.edu.xmu.oomall.wechatpay.microservice;

import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPaymentNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundNotifyRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@FeignClient(value = "payment-service")
public interface WeChatPayNotifyService
{
    @PostMapping("/wechat/payment/notify")
    InternalReturnObject paymentNotify(@RequestBody WeChatPayPaymentNotifyRetVo weChatPayPaymentNotifyRetVo);

    @PostMapping("/wechat/refund/notify")
    InternalReturnObject refundNotify(@RequestBody WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo);
}
