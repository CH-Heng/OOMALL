package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author wwk
 * @date 2021/12/15
 */
@Component
@FeignClient(name = "shop-service")
public interface ShopService {

    /**
     * 内部API
     * 获得所有的shop
     * @return SimpleRetVo
     */
    @GetMapping("internal/shops/all")
    InternalReturnObject getShop(@RequestParam Integer page,
                                 @RequestParam Integer pageSize);
}
