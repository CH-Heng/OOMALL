package cn.edu.xmu.oomall.alipay.controller;

import cn.edu.xmu.oomall.alipay.util.WarpRetObject;
import cn.edu.xmu.oomall.alipay.service.AlipayService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Api(value = "支付宝接口", tags = "支付宝接口")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;



    @ApiOperation(value = "*AliPay支付",  produces="application/json;charset=UTF-8")
    @PostMapping("internal/alipay/gateway.do")
    public Object gatewayDo(@RequestParam(required = false) String app_id ,
                            @RequestParam(required = true) String method ,
                            @RequestParam(required = false) String format ,
                            @RequestParam(required = false) String charset  ,
                            @RequestParam(required = false) String sign_type  ,
                            @RequestParam(required = false) String sign  ,
                            @RequestParam(required = false) String timestamp  ,
                            @RequestParam(required = false) String notify_url   ,
                            @RequestParam(required = true) String biz_content
                            ) {
        WarpRetObject warpRetObject=new WarpRetObject();
        switch (method)
        {
            case "alipay.trade.wap.pay":
                warpRetObject.setPayRetVo(alipayService.pay(biz_content));
                break;
            case "alipay.trade.query":
                warpRetObject.setPayQueryRetVo(alipayService.payQuery(biz_content));
                break;
            case "alipay.trade.close":
                warpRetObject.setCloseRetVo(alipayService.close(biz_content));
                break;
            case "alipay.trade.refund":
                warpRetObject.setRefundRetVo(alipayService.refund(biz_content));
                break;
            case "alipay.trade.refund.query":
                warpRetObject.setRefundQueryRetVo(alipayService.refundQuery(biz_content));
                break;
            case"alipay.data.dataservice.bill.downloadurl.query":
                warpRetObject.setDownloadUrlQueryRetVo(alipayService.downloadUrlQuery());
                break;
            default:
                warpRetObject = new WarpRetObject();
        }
        return warpRetObject;
    }
}
