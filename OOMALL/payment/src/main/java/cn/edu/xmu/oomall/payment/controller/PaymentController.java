package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.oomall.payment.microservice.bo.NotifyBody;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayPaymentNotifyRetVo;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayRefundNotifyRetVo;
import cn.edu.xmu.oomall.payment.model.vo.*;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/02/22:26
 */
@Api(value = "支付服务", tags = "payment")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class PaymentController {
    private static final String WECHATPAY_TRADE_STATE_SUCCESS = "TRANSACTION.SUCCESS";
    private static final String ALIPAY_TRADE_STATE_SUCCESS = "TRADE_SUCCESS";
    @Autowired
    PaymentService paymentService;
    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得支付的所有状态
     * @return
     */
    @GetMapping("/payment/states")
    public Object getPaymentStates(){
        ReturnObject ret=paymentService.getPaymentStates();
        return Common.decorateReturnObject(ret);
    }

    /**
     * 获得退款的所有状态
     * @return
     */
    @GetMapping("/refund/states")
    public Object getRefundStates(){
        ReturnObject ret=paymentService.getRefundStates();
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员查询支付信息
     * @param shopId
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/payment")
    public Object getPaymentInfo(@PathVariable Long shopId, @RequestParam(required = false) String documentId, @RequestParam(required = false) Byte state, @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime beginTime, @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime endTime, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @LoginUser Long userId, @LoginName String userName){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=paymentService.getPaymentInfo((documentId==null||"".equals(documentId))?null:documentId,state,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员查询支付信息详情
     * @param shopId
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/payment/{id}")
    public Object getPaymentDetailInfo(@PathVariable Long shopId,@PathVariable Long id,@LoginUser Long userId, @LoginName String userName){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=paymentService.getPaymentDetailInfo(id);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员修改支付信息
     * @param shopId
     * @param id
     * @param userId
     * @param userName
     * @param modifyVo
     * @param bindingResult
     * @return
     */
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/payment/{id}")
    public Object modifyPayment(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long userId, @LoginName String userName, @Validated @RequestBody ModifyVo modifyVo, BindingResult bindingResult){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.modifyPayment(modifyVo,id,userId,userName);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员查询退款信息
     * @param shopId
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/refund")
    public Object getRefundInfo(@PathVariable Long shopId, @RequestParam(required = false) String documentId, @RequestParam(required = false) Integer state, @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime beginTime, @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime endTime, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @LoginUser Long userId, @LoginName String userName){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=paymentService.getRefundInfo((documentId==null||"".equals(documentId))?null:documentId,state==null?null:state.byteValue(),beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员查询退款信息详情
     * @param shopId
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/refund/{id}")
    public Object getRefundDetailInfo(@PathVariable Long shopId,@PathVariable Long id,@LoginUser Long userId, @LoginName String userName){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=paymentService.getRefundDetailInfo(id);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 平台管理员修改退款信息
     * @param shopId
     * @param id
     * @param userId
     * @param userName
     * @param modifyVo
     * @param bindingResult
     * @return
     */
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/refund/{id}")
    public Object modifyRefund(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long userId, @LoginName String userName, @Validated @RequestBody ModifyVo modifyVo, BindingResult bindingResult){
        if (0!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.modifyRefund(modifyVo,id,userId,userName);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 顾客支付已建立的支付单
     * @param pid
     * @param userId
     * @param userName
     * @param paymentPatternVo
     * @param bindingResult
     * @return
     */
    @Audit
    @PutMapping("/payments/{pid}/pay")
    public Object payPayment(@PathVariable Long pid, @LoginUser Long userId, @LoginName String userName, @Validated @RequestBody PaymentPatternVo paymentPatternVo, BindingResult bindingResult){
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.payPayment(pid,paymentPatternVo,userId);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 微信支付通知API
     * @param weChatPayPaymentNotifyRetVo
     * @return
     */
    @PostMapping("/wechat/payment/notify")
    public Object paymentNotify(@RequestBody WeChatPayPaymentNotifyRetVo weChatPayPaymentNotifyRetVo){
        ReturnObject ret = new ReturnObject();
        if (WECHATPAY_TRADE_STATE_SUCCESS.equals(weChatPayPaymentNotifyRetVo.getEventType())){
            ret = paymentService.paymentSuccess(weChatPayPaymentNotifyRetVo.getResource().getCiphertext().getOutTradeNo());
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * 微信退款通知API
     * @param weChatPayRefundNotifyRetVo
     * @return
     */
    @PostMapping("/wechat/refund/notify")
    public Object refundNotify(@RequestBody WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo){
        ReturnObject ret = new ReturnObject();
        if (WECHATPAY_TRADE_STATE_SUCCESS.equals(weChatPayRefundNotifyRetVo.getEventType())){
            ret = paymentService.refundSuccess(weChatPayRefundNotifyRetVo.getResource().getCiphertext().getOutRefundNo());
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * 支付宝异步通知API
     * @param notifyBody
     * @return
     */
    @PostMapping(value = "/alipay/notify")
    public Object alipayNotify(@RequestBody NotifyBody notifyBody){
        ReturnObject ret = new ReturnObject();
        //如果out_biz_no字段不为空 意味着这是退款回调
        if (notifyBody.getOut_biz_no()==null){
            if (notifyBody.getTrade_status()!=null&&ALIPAY_TRADE_STATE_SUCCESS.equals(notifyBody.getTrade_status())){
                ret = paymentService.paymentSuccess(notifyBody.getOut_trade_no());
            }
        }else   {
            if (notifyBody.getRefund_fee()!=null){
                ret =paymentService.refundSuccess(notifyBody.getOut_trade_no());
            }
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * 顾客支付
     * @param userId
     * @param userName
     * @param paymentVo
     * @param bindingResult
     * @return
     */
    @Audit
    @PostMapping("/payments")
    public Object payment(@LoginUser Long userId, @LoginName String userName, @Validated @RequestBody PaymentVo paymentVo, BindingResult bindingResult){
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.payment(paymentVo,userId,userName);
        if(ret.getCode()==ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * 内部API-用于管理员为顾客建立售后支付
     * @param userId
     * @param userName
     * @param paymentVo
     * @param bindingResult
     * @return
     */
    @Audit
    @PostMapping("/internal/payments")
    public Object paymentWithCustomer(@LoginUser Long userId, @LoginName String userName, @Validated @RequestBody PaymentVo paymentVo, BindingResult bindingResult){
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.payment(paymentVo,userId,userName);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 内部退款api
     * @param userId
     * @param userName
     * @param refundVo
     * @param bindingResult
     * @return
     */
    @Audit
    @PostMapping("/internal/refund")
    public Object refund(@LoginUser Long userId, @LoginName String userName, @Validated @RequestBody RefundVo refundVo, BindingResult bindingResult){
        var res =Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        ReturnObject ret = paymentService.refund(refundVo,userId,userName);
        return Common.decorateReturnObject(ret);
    }


    /**
     * 内部API-顾客查看支付信息
     * @return
     */
    @Audit
    @GetMapping("/internal/payments")
    public Object findPaymentWithCustomer(@RequestParam("documentId") String documentId){
        ReturnObject ret = paymentService.getPayment(documentId);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 内部退款api-顾客查看退款信息
     * @return
     */
    @Audit
    @GetMapping("/internal/refunds")
    public Object findRefundWithCustomer(@RequestParam("documentId") String documentId){
        ReturnObject ret = paymentService.getRefund(documentId);
        return Common.decorateReturnObject(ret);
    }

}
