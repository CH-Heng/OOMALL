package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "aftersale-service")
public interface AftersaleService {
    /**
     * 内部API
     * 根据sn获取售后单
     * @return CustomerRetVo
     */
    @GetMapping("/internal/aftersale")
    InternalReturnObject getAftersaleBySn(@RequestParam(value = "sn") String sn);
}