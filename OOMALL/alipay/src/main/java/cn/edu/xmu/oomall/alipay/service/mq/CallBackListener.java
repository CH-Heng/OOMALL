package cn.edu.xmu.oomall.alipay.service.mq;

import cn.edu.xmu.oomall.alipay.microservice.PaymentFeightService;
import cn.edu.xmu.oomall.alipay.model.bo.NotifyBody;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/26/11:13
 */
@Service
@RocketMQMessageListener(topic = "alipay-callback-topic",consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "alipay-callback-group")
public class CallBackListener implements RocketMQListener<String> {
    @Autowired
    PaymentFeightService paymentFeightService;

    @Override
    public void onMessage(String notifyBody){
        System.out.println("成功收到消息");
        NotifyBody notifyBody1 = JacksonUtil.toObj(notifyBody,NotifyBody.class);
        paymentFeightService.notify(notifyBody1);
    }

}
