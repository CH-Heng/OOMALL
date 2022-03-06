package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPaymentNotifyRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RocketMQMessageListener(topic = "wechatpaypayment-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechatpaypayment-group")
public class PaymentNotifyService implements RocketMQListener<String> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    public void onMessage(String weChatPayPaymentNotifyRetVo) {
        WeChatPayPaymentNotifyRetVo notifyBody = JacksonUtil.toObj(weChatPayPaymentNotifyRetVo, WeChatPayPaymentNotifyRetVo.class);
        weChatPayNotifyService.paymentNotify(notifyBody);
    }
}