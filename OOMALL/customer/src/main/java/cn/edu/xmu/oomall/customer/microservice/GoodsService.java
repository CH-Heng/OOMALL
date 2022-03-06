package cn.edu.xmu.oomall.customer.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "goods-service")
public interface GoodsService {
    /**
     * 获取商品基本信息
     * @param id
     * @return
     */
    @GetMapping("/products/{id}")
    ReturnObject getProduct(@PathVariable Long id);
}
