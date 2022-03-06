package cn.edu.xmu.oomall.customer.microservice;

import cn.edu.xmu.oomall.customer.model.vo.CouponStateVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(value = "coupon-service")
public interface CouponService {
    /**
     * 获取优惠活动
     */
    @GetMapping("/products/{id}/couponactivities")
    ReturnObject getCouponActivityById(@PathVariable Long id);
    /**
     * 获得优惠券的基本信息
     */
    @GetMapping("/internal/couponactivity/{id}")
    ReturnObject getActivityByProduct(@PathVariable Long productId);
}
