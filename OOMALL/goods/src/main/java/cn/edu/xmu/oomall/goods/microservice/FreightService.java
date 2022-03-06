package cn.edu.xmu.oomall.goods.microservice;


import cn.edu.xmu.oomall.goods.microservice.vo.FreightModelRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wyg
 */
@FeignClient(value = "freight-service")
public interface FreightService {
    @GetMapping("/shops/{shopId}/freightmodels/default")
    InternalReturnObject<FreightModelRetVo> getDefaultFreightModel(@PathVariable("shopId") Long shopId);

    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    InternalReturnObject<FreightModelRetVo> getFreightModel(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id);

    @GetMapping("/freightmodels/{id}/exist")
    InternalReturnObject<Boolean> existFreightModel(@PathVariable("id") Long id);
}
