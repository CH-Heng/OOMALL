package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayFundFlowBillRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayTransactionVo;
import cn.edu.xmu.oomall.wechatpay.service.WeChatPayService;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayCommon;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.processFieldErrors;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class WeChatPayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    public Object createTransaction(@Validated @RequestBody WeChatPayTransactionVo weChatPayTransactionVo, BindingResult bindingResult){

        Object object = processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return WeChatPayCommon.decorateReturnObject(new WeChatPayReturnObject(WeChatPayReturnNo.PARAM_ERROR));
        }

        WeChatPayReturnObject returnObject = weChatPayService.createTransaction(new WeChatPayTransaction(weChatPayTransactionVo));
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    public Object getTransaction(@PathVariable("out_trade_no") String outTradeNo){

        WeChatPayReturnObject returnObject = weChatPayService.getTransaction(outTradeNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}/close")
    public Object closeTransaction(@PathVariable("out_trade_no") String outTradeNo){

        WeChatPayReturnObject returnObject = weChatPayService.closeTransaction(outTradeNo);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/refund/domestic/refunds")
    public Object createRefund(@Validated @RequestBody WeChatPayRefundVo weChatPayRefundVo, BindingResult bindingResult){

        Object object = processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return WeChatPayCommon.decorateReturnObject(new WeChatPayReturnObject(WeChatPayReturnNo.PARAM_ERROR));
        }

        WeChatPayReturnObject returnObject = weChatPayService.createRefund(new WeChatPayRefund(weChatPayRefundVo));
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/refund/domestic/refunds/{out_refund_no}")
    public Object getRefund(@PathVariable("out_refund_no") String outRefundNo){

        WeChatPayReturnObject returnObject = weChatPayService.getRefund(outRefundNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/bill/fundflowbill")
    public Object getFundFlowBill(@RequestParam("bill_date") String billDate){

        WeChatPayReturnObject returnObject = new WeChatPayReturnObject(new WeChatPayFundFlowBillRetVo());
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

}
