package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundNotifyRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RocketMQMessageListener(topic = "wechatpayrefund-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechatpayrefund-group")
public class RefundNotifyService implements RocketMQListener<String> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    @Override
    public void onMessage(String weChatPayRefundNotifyRetVo) {
        WeChatPayRefundNotifyRetVo notifyBody = JacksonUtil.toObj(weChatPayRefundNotifyRetVo,WeChatPayRefundNotifyRetVo.class);
        weChatPayNotifyService.refundNotify(notifyBody);
    }
}