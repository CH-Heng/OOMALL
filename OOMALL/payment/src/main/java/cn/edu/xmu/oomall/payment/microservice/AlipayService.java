package cn.edu.xmu.oomall.payment.microservice;

import cn.edu.xmu.oomall.payment.microservice.util.AlipayReturnNo;
import cn.edu.xmu.oomall.payment.microservice.util.WarpRetObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/8 11:04
 **/
@Component
@FeignClient(value = "alipay-service")
public interface AlipayService {
    @ApiOperation(value = "*AliPay支付",  produces="application/json;charset=UTF-8")
    @PostMapping("internal/alipay/gateway.do")
    public InternalReturnObject<WarpRetObject> gatewayDo(@RequestParam(required = false) String app_id,
                                                 @RequestParam(required = true) String method,
                                                 @RequestParam(required = false) String format,
                                                 @RequestParam(required = false) String charset,
                                                 @RequestParam(required = false) String sign_type,
                                                 @RequestParam(required = false) String sign,
                                                 @RequestParam(required = false) String timestamp,
                                                 @RequestParam(required = false) String notify_url,
                                                 @RequestParam(required = true) String biz_content);

}
