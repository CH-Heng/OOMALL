package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.vo.OrderInfoVo;
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
@RocketMQMessageListener(topic = "orders-cancel-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "orders-group")
public class OrderCancelListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Override
    public void onMessage(String s) {
        OrderInfoVo orderInfoVo = JacksonUtil.toObj(s, OrderInfoVo.class);
        String orderSn = orderInfoVo.getOrderSn();
        ReturnObject<List<Order>> returnObject=orderDao.showOrdersByOrderSn(orderSn);
        List<Order>orders = returnObject.getData();
        for(Order order:orders){
            //如果还未支付，就取消订单
            if(order.getState().equals(Order.State.NEW.getCode()) ||
                    order.getState().equals(Order.State.TO_BE_PAID.getCode())){
                orderDao.cancelOrderById(order.getId());
            }
        }
    }
}
