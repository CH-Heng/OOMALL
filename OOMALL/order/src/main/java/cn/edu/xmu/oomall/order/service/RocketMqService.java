package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.order.model.bo.OrderInfo;
import cn.edu.xmu.oomall.order.model.vo.OrderInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/9
 */
@Service
public class RocketMqService {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Value("${rocketmq.order-cancel-topic.delay-level}")
    private int delayLevel;

    @Value("${rocketmq.order-cancel-topic.timeout}")
    private long timeout;

    /**
     * 发送取消订单的消息-延时
     * @param orderInfoVo
     */
    public void sendCancelOrderMessage(OrderInfoVo orderInfoVo){

        String json = JacksonUtil.toJson(orderInfoVo);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.asyncSend("orders-cancel-topic", message, new SendCallback() {

            /**
             * 回调函数中的成功回调
             * @param sendResult 发送结果
             */
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendCancelOrderMessage: onSuccess result = "+ sendResult+" time ="+ LocalDateTime.now());
            }

            /**
             * 回调函数中的异常回调
             * @param throwable 异常结果
             */
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendCancelOrderMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout,delayLevel);
    }

    /**
     * 发送新建订单的消息-异步
     * @param orderInfo
     */
    public void sendPostOrderMessage(OrderInfo orderInfo){

        String json = JacksonUtil.toJson(orderInfo);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.asyncSend("orders-post-topic", message, new SendCallback() {

            /**
             * 回调函数中的成功回调
             * @param sendResult 发送结果
             */
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendPostOrderMessage: onSuccess result = "+ sendResult+" time ="+ LocalDateTime.now());
            }

            /**
             * 回调函数中的异常回调
             * @param throwable 异常结果
             */
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendPostOrderMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout);
    }

}
