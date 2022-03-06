package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
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
@FeignClient(name = "goods-service")
public interface GoodsService {

    /**
     * 内部API
     * 获得商品的详细信息
     * @return ProductRetVo
     */
    @GetMapping("internal/products/{id}")
    InternalReturnObject getProduct(@PathVariable Long id);
}
