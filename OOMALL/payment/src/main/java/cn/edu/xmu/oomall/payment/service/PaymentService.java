package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.oomall.payment.microservice.OrderService;
import cn.edu.xmu.oomall.payment.microservice.vo.*;
import cn.edu.xmu.oomall.payment.dao.PaymentDao;
import cn.edu.xmu.oomall.payment.dao.PaymentPatternDao;
import cn.edu.xmu.oomall.payment.microservice.AlipayService;
import cn.edu.xmu.oomall.payment.microservice.WechatPayService;
import cn.edu.xmu.oomall.payment.microservice.bo.NotifyBody;
import cn.edu.xmu.oomall.payment.model.bo.*;
import cn.edu.xmu.oomall.payment.model.po.PaymentPatternPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPo;
import cn.edu.xmu.oomall.payment.model.po.RefundPo;
import cn.edu.xmu.oomall.payment.model.vo.*;
import cn.edu.xmu.oomall.payment.model.vo.RefundRetVo;
import cn.edu.xmu.oomall.payment.model.vo.RefundVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.concatString;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/02/22:37
 */
@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;

    @Autowired
    PaymentPatternDao paymentPatternDao;

    @Resource
    AlipayService alipayService;

    @Resource
    WechatPayService wechatPayService;

    @Resource
    OrderService orderService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    /**
     * 获取支付的全部状态
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentStates() {
        return paymentDao.getPaymentStates();
    }

    /**
     * 获取退款的全部状态
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getRefundStates() {
        return paymentDao.getRefundStates();
    }

    /**
     * 获取支付信息
     * @param documentId
     * @return
     */
    public ReturnObject getPayment(String documentId){
        return paymentDao.getPayment(documentId);
    }

    /**
     * 获取退款信息
     * @param documentId
     * @return
     */
    public ReturnObject getRefund(String documentId){
        return paymentDao.getRefund(documentId);
    }


    /**
     * 获取支付信息
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentInfo(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        ReturnObject ret = paymentDao.getPaymentInfo(documentId,state,beginTime,endTime,page,pageSize);
        return ret;
    }

    /**
     * 获取单个支付信息明细
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentDetailInfo(Long id) {
        ReturnObject ret = paymentDao.getPaymentDetailInfo(id);
        if (ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        PaymentPo paymentPo = (PaymentPo) ret.getData();
        PaymentRetVo paymentRetVo = cloneVo(paymentPo,PaymentRetVo.class);
        return new ReturnObject(paymentRetVo);
    }

    /**
     * 修改支付
     * @param modifyVo
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject modifyPayment(ModifyVo modifyVo, Long id, Long userId, String userName) {
        PaymentPo paymentPo = cloneVo(modifyVo,PaymentPo.class);
        paymentPo.setId(id);
        ReturnObject ret = paymentDao.modifyPayment(paymentPo);
        if (ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        ReturnObject ret1 = paymentDao.getPaymentDetailInfo(id);
        if (ret1.getCode() != ReturnNo.OK) {
            return ret1;
        }
        PaymentPo paymentPo1 = (PaymentPo) ret1.getData();
        PaymentRetVo paymentRetVo = cloneVo(paymentPo1,PaymentRetVo.class);
        return new ReturnObject(paymentRetVo);
    }

    /**
     * 获取退款信息
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getRefundInfo(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        ReturnObject ret = paymentDao.getRefundInfo(documentId,state,beginTime,endTime,page,pageSize);
        return ret;
    }

    /**
     * 获取退款信息明细
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getRefundDetailInfo(Long id) {
        ReturnObject ret = paymentDao.getRefundDetailInfo(id);
        if (ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        RefundPo refundPo = (RefundPo) ret.getData();
        RefundRetVo refundRetVo = cloneVo(refundPo, RefundRetVo.class);
        return new ReturnObject(refundRetVo);
    }

    /**
     * 修改退款
     * @param modifyVo
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject modifyRefund(ModifyVo modifyVo, Long id, Long userId, String userName) {
        RefundPo refundPo = cloneVo(modifyVo,RefundPo.class);
        refundPo.setId(id);
        ReturnObject ret = paymentDao.modifyRefund(refundPo);
        if (ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        ReturnObject ret1= paymentDao.getRefundDetailInfo(id);
        if (ret1.getCode() != ReturnNo.OK) {
            return ret1;
        }
        RefundPo paymentPo1 = (RefundPo) ret1.getData();
        RefundRetVo refundRetVo = cloneVo(paymentPo1,RefundRetVo.class);
        return new ReturnObject(refundRetVo);
    }

    /**
     * 支付一个已经创建的支付单
     * 可能是以下的情况
     * 1.订单 2.保证金 3.订单定金 4.订单尾款
     * 应该覆盖以下字段:
     * 1.outTradeNo
     * 2.amount
     * 3.actualAmount
     * @param pid
     * @param paymentPatternVo
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject payPayment(Long pid , PaymentPatternVo paymentPatternVo, Long userId) {
        ReturnObject paymentRet = paymentDao.getPaymentDetailInfo(pid);
        ReturnObject patternRet = paymentPatternDao.getPaymentPattern(paymentPatternVo.getPayPattern());
        if (paymentRet.getCode() != ReturnNo.OK) {
            return paymentRet;
        }
        if (patternRet.getCode() != ReturnNo.OK) {
            return patternRet;
        }
        PaymentPatternPo paymentPatternPo = (PaymentPatternPo) patternRet.getData();
        PaymentPo paymentPo = (PaymentPo) paymentRet.getData();
        if (!userId.equals(paymentPo.getCreatorId())) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        if (!Payment.State.UNPAID.getCode().equals(paymentPo.getState())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        //开始支付,调用第三方平台
        String outTradeNo = Common.genSeqNum(1);
        InternalReturnObject ret;
        String className = paymentPatternPo.getClassName();
        switch (className){
            case "alipay": {
                PayVo payVo = new PayVo(outTradeNo,paymentPo.getAmount());
                ret = alipayService.gatewayDo("20214072300007148","alipay.trade.wap.pay","JSON","utf-8", "RSA2","asdfghjkl",LocalDateTime.now().toString(),"https://m.alipay.com/Gk8NF23", JacksonUtil.toJson(payVo));
                break;
            }
            case "wechatpay":{
                WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
                vo.setAppid("wxd678efh567hg6787");
                vo.setMchid("1230000109");
                vo.setDescription("pay");
                vo.setTimeExpire(LocalDateTime.now());
                vo.setAmount(new TransactionAmountVo(100,"CNY"));
                vo.setPayer(new PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
                vo.setNotifyUrl("/wechat/payment/notify");
                ret = wechatPayService.createTransaction(vo);
                break;
            }
            default: break;
        }
        //设置out_trade_no,支付方式id和支付时间 将其写入数据库中
        paymentPo.setTradeSn(outTradeNo);
        paymentPo.setPatternId(paymentPatternVo.getPayPattern());
        paymentPo.setPayTime(LocalDateTime.now());
        ReturnObject ret1 = paymentDao.modifyPayment(paymentPo);
        if (ret1.getCode() != ReturnNo.OK) {
            return ret1;
        }
        //异步查询是否正确回调
        PaymentNotifyBody paymentNotifyBody = new PaymentNotifyBody(outTradeNo,paymentPo.getPatternId(),(byte)0);
        String json = JacksonUtil.toJson(paymentNotifyBody);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("payment-callback-topic", message);
        PaymentSimpleRetVo paymentRetVo = cloneVo(paymentPo,PaymentSimpleRetVo.class);
        //异步回调之后再修改支付的状态

        return new ReturnObject(paymentRetVo);
    }

    /**
     * 买家发起支付或者管理员为用户发起支付
     * @param paymentVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject payment(PaymentVo paymentVo, Long userId, String userName) {
        //创建支付单
        PaymentPo paymentPo = cloneVo(paymentVo, PaymentPo.class);
        Common.setPoCreatedFields(paymentPo,userId,userName);
        paymentPo.setState((byte)0);
        paymentPo.setActualAmount(paymentPo.getAmount());
        ReturnObject newPaymentRet = paymentDao.newPayment(paymentPo);
        if (newPaymentRet.getCode() != ReturnNo.OK) {
            return newPaymentRet;
        }
        PaymentPo newPayment = (PaymentPo) newPaymentRet.getData();
//        ReturnObject patternRet = paymentPatternDao.getPaymentPattern(newPayment.getPatternId());
//        if (patternRet.getCode() != ReturnNo.OK) {
//            return patternRet;
//        }
//        PaymentPatternPo paymentPatternPo = (PaymentPatternPo) patternRet.getData();
        //开始支付,调用第三方平台
        String outTradeNo = Common.genSeqNum(1);
        InternalReturnObject ret;
//        String className = paymentPatternPo.getClassName();
        switch (newPayment.getPatternId().intValue()){
            case 0: {
                PayVo payVo = new PayVo(outTradeNo,newPayment.getAmount());
                ret = alipayService.gatewayDo("20214072300007148","alipay.trade.wap.pay","JSON","utf-8", "RSA2","asdfghjkl",LocalDateTime.now().toString(),"https://m.alipay.com/Gk8NF23", JacksonUtil.toJson(payVo));
                break;
            }
            case 1: {
                WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
                vo.setAppid("wxd678efh567hg6787");
                vo.setMchid("1230000109");
                vo.setDescription("pay");
                vo.setTimeExpire(LocalDateTime.now());
                vo.setAmount(new TransactionAmountVo(newPayment.getAmount().intValue(),"CNY"));
                vo.setPayer(new PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
                vo.setNotifyUrl("/wechat/payment/notify");
                vo.setOutTradeNo(outTradeNo);
                ret = wechatPayService.createTransaction(vo);
                break;
            }
            default: break;
        }
        //设置out_trade_no,支付方式id和支付时间 将其写入数据库中
//        orderService.seperateOrdersByOrderSn(paymentVo.getDocumentId());
        newPayment.setTradeSn(outTradeNo);
        newPayment.setPatternId(newPayment.getPatternId());
        newPayment.setPayTime(LocalDateTime.now());
        ReturnObject ret1 = paymentDao.modifyPayment(newPayment);
        if (ret1.getCode() != ReturnNo.OK) {
            return ret1;
        }
        //异步查询是否正确回调
        PaymentNotifyBody paymentNotifyBody = new PaymentNotifyBody(outTradeNo,paymentPo.getPatternId(),(byte)0);
        String json = JacksonUtil.toJson(paymentNotifyBody);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("payment-callback-topic", message);
        PaymentSimpleRetVo paymentRetVo = cloneVo(newPayment,PaymentSimpleRetVo.class);
        //异步回调之后再修改支付的状态
        return new ReturnObject(paymentRetVo);
    }

    /**
     * 内部发起退款请求
     * @param refundVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject refund(RefundVo refundVo, Long userId, String userName) {
        PaymentPo paymentPo = cloneVo(refundVo,PaymentPo.class);
        ReturnObject ret = paymentDao.getPaymentsByPo(paymentPo);
        if (ret.getData()==null){
            return ret;
        }
        PaymentPo paymentPo1 = (PaymentPo) ret.getData();
        //创建退款单
        paymentPo1.setAmount(refundVo.getAmount()==null?paymentPo1.getAmount():refundVo.getAmount());
        String outTradeNo = Common.genSeqNum(1);
        RefundPo refundPo = new RefundPo();
        refundPo.setState((byte)1);
        refundPo.setPaymentId(paymentPo1.getId());
        refundPo.setDocumentId(paymentPo1.getDocumentId());
        refundPo.setDocumentType(paymentPo1.getDocumentType());
        refundPo.setPatternId(paymentPo1.getPatternId());
        refundPo.setAmount(paymentPo1.getAmount());
        refundPo.setTradeSn(outTradeNo);
        Common.setPoCreatedFields(refundPo,userId,userName);
        //新建退款单
        ReturnObject insertRet = paymentDao.createRefund(refundPo);
        if (insertRet.getData()==null){
            return insertRet;
        }
        RefundPo insertRefund = (RefundPo) insertRet.getData();
        //调用第三方的退款接口
        ReturnObject patternRet = paymentPatternDao.getPaymentPattern(paymentPo1.getPatternId());
        if (patternRet.getData()==null){
            return patternRet;
        }
        PaymentPatternPo paymentPatternPo = (PaymentPatternPo) patternRet.getData();
        //根据类名调用退款接口
        InternalReturnObject thirdPartyRet ;
        switch (paymentPatternPo.getClassName()){
            case "alipay":{
                cn.edu.xmu.oomall.payment.microservice.vo.RefundVo refundVo1 = new cn.edu.xmu.oomall.payment.microservice.vo.RefundVo(paymentPo1.getTradeSn(),outTradeNo,insertRefund.getAmount());
                thirdPartyRet = alipayService.gatewayDo("20214072300007148","alipay.trade.wap.refund","JSON","utf-8", "RSA2",
                        "asdfghjkl",LocalDateTime.now().toString(),"https://m.alipay.com/Gk8NF23", JacksonUtil.toJson(refundVo1));
                break;
            }
            case "wechatpay":{
                RefundAmountVo refundAmountVo = new RefundAmountVo(insertRefund.getAmount().intValue(),paymentPo1.getAmount().intValue(),"CNY");
                WeChatPayRefundVo weChatPayRefundVo = new WeChatPayRefundVo();
                weChatPayRefundVo.setOutTradeNo(paymentPo1.getTradeSn());
                weChatPayRefundVo.setOutRefundNo(outTradeNo);
                weChatPayRefundVo.setNotifyUrl("/wechat/payment/notify");
                weChatPayRefundVo.setReason("微信退款测试");
                weChatPayRefundVo.setAmount(refundAmountVo);
                thirdPartyRet = wechatPayService.createRefund(weChatPayRefundVo);
                break;
            }
            default: break;
        }
        return new ReturnObject();
    }

    /**
     * 统一支付成功的方法
     * 修改支付单状态
     * 通知订单分单
     * @param outTradeNo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject paymentSuccess(String outTradeNo){
        ReturnObject ret = paymentDao.getPaymentByOutTradeNo(outTradeNo);
        if (ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        PaymentPo paymentPo = (PaymentPo) ret.getData();
        if (Payment.State.UNPAID.getCode().byteValue()!=paymentPo.getState()) {
            return new ReturnObject();
        }
        //修改支付的状态
        paymentPo.setState(Payment.State.PAID.getCode().byteValue());
        paymentPo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = paymentDao.modifyPayment(paymentPo);
        if (returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        //如果是一个预售订单 需要创建一个尾款支付单
        if (DocumentType.ORDERHEAD.getCode().byteValue()==paymentPo.getDocumentType()){
            //创建一个支付单,订单类型为尾款单, 关联好单据ID和单据类型
            PaymentPo newPaymentPo = new PaymentPo();
            newPaymentPo.setDocumentId(paymentPo.getDocumentId());
            newPaymentPo.setDocumentType(DocumentType.ORDERTAIL.getCode());
            newPaymentPo.setState(Payment.State.UNPAID.getCode());
            paymentDao.newPayment(paymentPo);
        }

        //分单
        SimpleSeperateOrderVo simpleSeperateOrderVo = new SimpleSeperateOrderVo(paymentPo.getDocumentId(),paymentPo.getDocumentType());
        String json = JacksonUtil.toJson(simpleSeperateOrderVo);
//        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("order-seperate-topic", json);
        //异步通知支付成功
        SimplePaymentVo paymentVo = cloneVo(paymentPo,SimplePaymentVo.class);
        String json1 = JacksonUtil.toJson(paymentVo);
//        Message message1 = MessageBuilder.withPayload(json1).build();
        rocketMQTemplate.sendOneWay("payment-success-topic", json1);
        return new ReturnObject();
    }

    /**
     * 统一退款成功的方法
     * 异步通知退款成功
     * @param outTradeNo
     * @return
     */
    @Transactional
    public ReturnObject refundSuccess(String outTradeNo){
        ReturnObject ret = paymentDao.getRefundByOutTradeNo(outTradeNo);
        if (ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        RefundPo refundPo = (RefundPo) ret.getData();
        if (Refund.State.UNREFUNDED.getCode().byteValue()!=refundPo.getState()) {
            return new ReturnObject();
        }
        //修改退款
        refundPo.setState(Refund.State.REFUNDED.getCode().byteValue());
        refundPo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = paymentDao.modifyRefund(refundPo);
        if (returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        //异步通知退款成功
        SimpleRefundVo refundVo = cloneVo(refundPo,SimpleRefundVo.class);
        String json = JacksonUtil.toJson(refundVo);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("refund-success-topic", message);
        return new ReturnObject();
    }
}
