package cn.edu.xmu.oomall.coupon.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.PageVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author qingguo Hu 22920192204208
 */
@Component
@FeignClient(value = "goods-service")
public interface GoodsService {

    @ApiOperation(value = "通过Id返回OnsaleVo")
    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnsaleVo> getOnsaleById(@PathVariable("id") Long id);

    @ApiOperation(value = "通过productId查OnsaleVo（1对多）")
    @GetMapping("/internal/onsales")
    InternalReturnObject<PageVo<OnsaleVo>> listOnsale(@RequestParam("productId") Long productId,
                                                      @RequestParam("page") Integer pageNumber,
                                                      @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/products/{id}")
    InternalReturnObject<ProductRetVo> getProductById(@PathVariable("id")Long id);
}
