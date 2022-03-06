package cn.edu.xmu.oomall.customer.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.oomall.customer.service.CouponsService;
import cn.edu.xmu.oomall.customer.service.CustomerService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import lombok.extern.java.Log;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@RestController
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
public class CouponController {
    @Autowired
    CouponsService couponsService;

    /**
     * 买家查看优惠券列表
     */
    @ApiOperation(value = "买家查看优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = true,dataType = "Integer", paramType = "param"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = true,dataType = "Integer", paramType = "param")
    })
    @Audit
    @GetMapping(value = "coupons")
    public Object showCoupons(@LoginUser Long userId,
                              @LoginName String userName,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize) {
        ReturnObject returnObject = couponsService.showCoupons(userId, page, pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     *买家领取优惠券
     */
    @ApiOperation(value = "买家领取优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "活动ID", required = true,dataType = "Long", paramType = "path")
    })
    @Audit
    @PostMapping(value = "couponactivities/{id}/coupons")
    public Object getCoupon(@LoginUser Long userId,
                            @LoginName String userName,
                            @PathVariable Long id){
        return Common.decorateReturnObject(couponsService.receiveCoupon(userId,userName,id));
    }
}
