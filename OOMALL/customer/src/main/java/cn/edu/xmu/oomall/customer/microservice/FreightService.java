package cn.edu.xmu.oomall.customer.microservice;

import cn.edu.xmu.oomall.customer.microservice.Vo.RegionRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
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

    /**
     * 内部API
     * 获得地区的信息
     * @return RegionSimpleRetVo
     */
    @GetMapping("internal/region/{id}")
    InternalReturnObject<RegionRetVo> getRegionById(@PathVariable Long id);
}

