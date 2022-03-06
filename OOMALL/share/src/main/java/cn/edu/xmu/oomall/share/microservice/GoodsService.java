package cn.edu.xmu.oomall.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.share.microservice.vo.OnsaleRetVo;
import cn.edu.xmu.oomall.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "goods-service")
public interface GoodsService {

    @GetMapping( "/internal/onsales/{id}")
    InternalReturnObject<OnsaleRetVo> selectFullOnsale(@PathVariable Long id);

    @GetMapping("/products/{id}")
    ReturnObject<ProductRetVo> getProductDetails(@PathVariable Long id);
}