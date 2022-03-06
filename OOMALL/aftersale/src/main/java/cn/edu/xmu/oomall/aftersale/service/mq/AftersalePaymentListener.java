package cn.edu.xmu.oomall.aftersale.service.mq;

import cn.edu.xmu.oomall.aftersale.dao.AftersaleDao;
import cn.edu.xmu.oomall.aftersale.microservice.vo.SimplePaymentVo;
import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RocketMQMessageListener(topic = "payment-success-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "payments-group")
public class AftersalePaymentListener implements RocketMQListener<String> {
    @Autowired
    AftersaleDao aftersaleDao;

    private static final Logger logger = LoggerFactory.getLogger(AftersalePaymentListener.class);

    @Override
    public void onMessage(String message) {
        try {
            SimplePaymentVo simplePaymentVo = JacksonUtil.toObj(message, SimplePaymentVo.class);
            ReturnObject returnObject = aftersaleDao.findAftersaleByServiceSn(simplePaymentVo.getDocumentId());
            if (returnObject == null) {
                logger.info("该支付单对应的售后单不存在");
            }else{
                AfterSalePo afterSalePo=(AfterSalePo) returnObject.getData();
                afterSalePo.setPrice(simplePaymentVo.getAmount());
                afterSalePo.setState(Aftersale.State.TO_BE_DELIVERED_BY_SHOPKEEPER.getCode());

                ReturnObject ret=aftersaleDao.updateByAftersaleIdSelective(afterSalePo);
                if(ret.getCode()==ReturnNo.OK){
                    logger.info("售后单状态和价格更新成功");
                }else{
                    logger.info("售后单更新失败");
                }
            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }
}
