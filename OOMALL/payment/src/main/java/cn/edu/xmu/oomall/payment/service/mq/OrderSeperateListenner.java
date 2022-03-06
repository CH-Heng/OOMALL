package cn.edu.xmu.oomall.payment.service.mq;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/22
 */
@Service
@RocketMQMessageListener(topic = "order-seperate-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "payment-seperate-group")
public class OrderSeperateListenner implements RocketMQListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(CallBackListener.class);
    @Override
    public void onMessage(String s) {
        String json = s;
        int length = json.length();
        logger.info(s);
        logger.info("lemgafasdfasdfsdfadfsdfsd");
    }
}
