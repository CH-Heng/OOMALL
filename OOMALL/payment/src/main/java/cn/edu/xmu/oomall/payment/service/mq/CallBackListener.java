package cn.edu.xmu.oomall.payment.service.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.dao.PaymentDao;
import cn.edu.xmu.oomall.payment.dao.PaymentPatternDao;
import cn.edu.xmu.oomall.payment.microservice.AlipayService;
import cn.edu.xmu.oomall.payment.microservice.WechatPayService;
import cn.edu.xmu.oomall.payment.microservice.bo.NotifyBody;
import cn.edu.xmu.oomall.payment.microservice.vo.PayVo;
import cn.edu.xmu.oomall.payment.microservice.vo.PayerVo;
import cn.edu.xmu.oomall.payment.microservice.vo.TransactionAmountVo;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayTransactionVo;
import cn.edu.xmu.oomall.payment.model.bo.Payment;
import cn.edu.xmu.oomall.payment.model.bo.PaymentNotifyBody;
import cn.edu.xmu.oomall.payment.model.bo.Refund;
import cn.edu.xmu.oomall.payment.model.po.PaymentPatternPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPo;
import cn.edu.xmu.oomall.payment.model.po.RefundPo;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/20/9:07
 */
@Service
@RocketMQMessageListener(topic = "payment-callback-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "payment-notify-group")
public class CallBackListener implements RocketMQListener<PaymentNotifyBody> {
    private static Logger logger = LoggerFactory.getLogger(CallBackListener.class);

    @Resource
    AlipayService alipayService;

    @Resource
    WechatPayService wechatPayService;

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    PaymentPatternDao paymentPatternDao;

    @Override
    public void onMessage(PaymentNotifyBody paymentNotifyBody) {
        //检查此时的支付表, 如果还没有变为已处理的状态, 说明没有正确回调, 需要重新query
        ReturnObject paymentRet = null;
        PaymentPo paymentPo = null;
        RefundPo refundPo = null;
        if (PaymentNotifyBody.State.PAYMENT.getCode().byteValue()==paymentNotifyBody.getType()){
            paymentRet = paymentDao.getPaymentByOutTradeNo(paymentNotifyBody.getTradeSn());
        }else{
            paymentRet = paymentDao.getRefundByOutTradeNo(paymentNotifyBody.getTradeSn());
        }
        if (paymentRet.getCode()!=ReturnNo.OK){
            return;
        }
        ReturnObject patternRet = paymentPatternDao.getPaymentPattern(paymentNotifyBody.getPatternId());
        if (patternRet.getCode()!=ReturnNo.OK){
            return;
        }
        PaymentPatternPo paymentPatternPo = (PaymentPatternPo) patternRet.getData();
        String className = paymentPatternPo.getClassName();
        if (PaymentNotifyBody.State.PAYMENT.getCode().byteValue()==paymentNotifyBody.getType())
        {
            paymentPo = (PaymentPo) paymentRet.getData();
            if (Payment.State.UNPAID.getCode().byteValue()!=paymentPo.getState()){
                return;
            }
            switch (className){
                case "alipay": {
                    PayVo payVo = new PayVo(paymentNotifyBody.getTradeSn(),null);
                    alipayService.gatewayDo("20214072300007148","alipay.trade.query","JSON","utf-8", "RSA2","asdfghjkl",LocalDateTime.now().toString(),"https://m.alipay.com/Gk8NF23", JacksonUtil.toJson(payVo));
                    break;
                }
                case "wechatpay":{
                    wechatPayService.getTransaction(paymentNotifyBody.getTradeSn());
                    break;
                }
                default: break;
            }
        }
        else
        {
            refundPo = (RefundPo) paymentRet.getData();
            if (Refund.State.UNREFUNDED.getCode().byteValue() != refundPo.getState()) {
                return;
            }
            switch (className){
                case "alipay": {
                    PayVo payVo = new PayVo(paymentNotifyBody.getTradeSn(),null);
                    alipayService.gatewayDo("20214072300007148","alipay.trade.refund.query","JSON","utf-8", "RSA2","asdfghjkl",LocalDateTime.now().toString(),"https://m.alipay.com/Gk8NF23", JacksonUtil.toJson(payVo));
                    break;
                }
                case "wechatpay":{
                    wechatPayService.getRefund(paymentNotifyBody.getTradeSn());
                    break;
                }
                default: break;
            }
        }
    }
}
