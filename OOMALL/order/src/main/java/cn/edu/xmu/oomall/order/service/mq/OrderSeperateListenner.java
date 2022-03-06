//package cn.edu.xmu.oomall.order.service.mq;
//
//import cn.edu.xmu.oomall.order.model.vo.SimpleSeperateOrderVo;
//import cn.edu.xmu.oomall.order.service.OrderService;
//import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author RenJieZheng 22920192204334
// * @date 2021/12/22
// */
//@Service
//@RocketMQMessageListener(topic = "order-seperate-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "payment-seperate-group")
//public class OrderSeperateListenner implements RocketMQListener<String> {
//    @Autowired
//    OrderService orderService;
//
//    @Override
//    public void onMessage(String s) {
//        SimpleSeperateOrderVo simpleSeperateOrderVo = JacksonUtil.toObj(s,SimpleSeperateOrderVo.class);
//        orderService.seperateOrdersByOrderSn(simpleSeperateOrderVo.getDocumentId(),simpleSeperateOrderVo.getDocumentType());
//    }
//}
