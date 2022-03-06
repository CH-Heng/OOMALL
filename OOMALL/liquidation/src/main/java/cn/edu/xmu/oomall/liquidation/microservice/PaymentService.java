package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.liquidation.constant.Constants;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Component
@FeignClient(name = "payment-service")
public interface PaymentService {

    /**
     * 内部API
     * 获得支付信息
     * @return PaymentRetVo
     */
    @GetMapping("/shops/{shopId}/payment")
    ReturnObject getPayment(@PathVariable Long shopId,
                            @DateTimeFormat(pattern = Constants.QUERY_DATE_TIME_FORMAT) @RequestParam ZonedDateTime beginTime,
                            @DateTimeFormat(pattern = Constants.QUERY_DATE_TIME_FORMAT) @RequestParam ZonedDateTime endTime,
                            @RequestParam Integer page,
                            @RequestParam Integer pageSize);

    /**
     * 内部API
     * 获得退款信息
     * @return RefundRetVo
     */
    @GetMapping("/shops/{shopId}/refund")
    ReturnObject getRefund(@PathVariable Long shopId,
                           @DateTimeFormat(pattern = Constants.QUERY_DATE_TIME_FORMAT) @RequestParam ZonedDateTime beginTime,
                           @DateTimeFormat(pattern = Constants.QUERY_DATE_TIME_FORMAT) @RequestParam ZonedDateTime endTime,
                           @RequestParam Integer page,
                           @RequestParam Integer pageSize);
}
