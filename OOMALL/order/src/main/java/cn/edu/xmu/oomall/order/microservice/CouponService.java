package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.DiscountItemVo;
import cn.edu.xmu.oomall.order.microservice.vo.DiscountRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Component
@FeignClient(value = "coupon-service")
public interface CouponService {

    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id couponActId
     * @return 是否存在
     */
    @GetMapping("/internal/couponactivity/{id}/exist")
    InternalReturnObject<Boolean> isCouponActivityExist(@PathVariable Long id);


    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id couponId
     * @return 是否存在
     */
    @GetMapping("/internal/coupon/{id}/exist")
    InternalReturnObject<Boolean> isCouponExist(@PathVariable Long id);

    /**
     * 计算最优优惠
     * @param items item
     * @return 优惠结果
     */
    @PutMapping("/internal/discountprices")
    InternalReturnObject<List<DiscountRetVo>> calculateDiscount(@RequestBody List<DiscountItemVo> items);
}
