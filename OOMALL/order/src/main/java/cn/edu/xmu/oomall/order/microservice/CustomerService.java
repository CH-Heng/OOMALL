package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.CustomerSimpleRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.CustomerPointRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.CustomerPointVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Component
@FeignClient(value = "customer-service")
public interface CustomerService {
    @GetMapping(value = "/internal/customers/{id}")
    InternalReturnObject<CustomerSimpleRetVo> getSimpleCustomer(@PathVariable Long id);


    /**
     * 修改返点
     * @param id
     * @return
     */
    @PutMapping(value = "/internal/customers/{id}/point")
    InternalReturnObject modifyCustomerPoint(@PathVariable Long id,@Validated @RequestBody CustomerPointVo vo);

    /**
     * 根据消费者id获得消费者信息
     * @param id
     * @return
     */
    @GetMapping(value = "/internal/customers/{id}/point")
    InternalReturnObject<CustomerPointRetVo> getPointByUserId(@PathVariable Long id);

    @ApiOperation(value = "使用优惠券")
    @PutMapping(value = "/internal/coupons/{id}/use")
    InternalReturnObject useCoupon(@PathVariable Long id);

    @ApiOperation(value = "恢复优惠券")
    @PutMapping(value = "/internal/coupons/{id}/renew")
    InternalReturnObject renewCoupon(@PathVariable Long id) ;

}
