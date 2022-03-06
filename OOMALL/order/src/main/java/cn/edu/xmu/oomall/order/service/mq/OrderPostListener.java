package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderInfo;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/9
 */
@Service
@RocketMQMessageListener(topic = "orders-post-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "order-group")
public class OrderPostListener implements RocketMQListener<String> {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Override
    public void onMessage(String s) {
        OrderInfo orderInfo = JacksonUtil.toObj(s, OrderInfo.class);
        Order order = orderInfo.getOrder();
        List<OrderItem>orderItems = orderInfo.getOrderItemList();
        ReturnObject returnObject = orderDao.addOrder(order);
        Order order1 = (Order) returnObject.getData();
        for(OrderItem orderItem:orderItems){
            orderItem.setOrderId(order1.getId());
            orderItemDao.addOrderItem(orderItem);
        }
        //顺便分担
        orderService.seperateOrdersByOrderSn(order.getOrderSn(),(byte)0);
    }
}
