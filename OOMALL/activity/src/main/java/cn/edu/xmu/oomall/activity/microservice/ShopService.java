package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Gao Yanfeng
 * @date 2021/11/12
 */
@FeignClient(value = "shop-service")
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleShopVo> getShopInfo(@PathVariable("id") Long id);

}
