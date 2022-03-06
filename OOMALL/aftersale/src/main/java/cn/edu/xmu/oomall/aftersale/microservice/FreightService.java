package cn.edu.xmu.oomall.aftersale.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wwk
 * @date 2021/12/11
 */
@Component
@FeignClient(name = "freight-service")
public interface FreightService {

    @ApiOperation(value = "内部API：获得地区的详细信息")
    @GetMapping("internal/region/{id}")
    InternalReturnObject getRegionById(@PathVariable("id") Long id);
}
