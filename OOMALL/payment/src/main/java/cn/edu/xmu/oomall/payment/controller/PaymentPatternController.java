package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.service.PaymentPatternService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/03/8:32
 */
@Api(value = "支付方式服务", tags = "comment")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class PaymentPatternController {

    @Autowired
    PaymentPatternService paymentPatternService;
    /**
     * 获得支付渠道的所有状态
     * @return
     */
    @GetMapping("/paypatterns/states")
    public Object getPaymentPatternSates(){
        ReturnObject ret=paymentPatternService.getPaymentPatternStates();
        return Common.decorateReturnObject(ret);
    }
    /**
     * 获得当前有效的支付渠道
     * @return
     */
    @GetMapping("/paypatterns")
    public Object getValidPayments(){
        ReturnObject ret=paymentPatternService.getValidPaymentPatterns();
        return Common.decorateReturnObject(ret);
    }

    /**
     *
     * @param shopId
     * @return
     */
    @GetMapping("/shops/{shopId}/paypatterns")
    public Object getShopPaymentPattern(@PathVariable Long shopId){
        if (0L!=shopId){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret= paymentPatternService.getPaymentPatterns();
        return Common.decorateReturnObject(ret);
    }
}
