package cn.edu.xmu.oomall.shop.microservice;

import cn.edu.xmu.oomall.shop.microservice.vo.RefundDepositVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "payment-service")
public interface PaymentService {

    @PostMapping("/internal/transfer")
    InternalReturnObject refund(@RequestBody RefundDepositVo refundDepositVo);
}