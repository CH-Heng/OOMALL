package cn.edu.xmu.oomall.customer.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.oomall.customer.service.AddressService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@RestController
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
public class AddressController {
    @Autowired
    AddressService addressService;

    /**
     * 买家新增地址
     */
    @ApiOperation(value = "买家新增地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addAddressVo", value = "地址信息", required = true,dataType = "AddAddressVo", paramType = "body")
    })
    @Audit
    @PostMapping("addresses")
    public Object addAddress(@LoginUser Long userId,
                             @LoginName String userName,
                             @RequestBody AddAddressVo addAddressVo,
                             HttpServletResponse httpServletResponse){
        return Common.decorateReturnObject(addressService.addAddress(userId,userName,addAddressVo));
    }

    /**
     * 买家设置默认地址
     */
    @ApiOperation(value = "买家设置默认地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "地址id", required = true,dataType = "Long", paramType = "path")
    })
    @Audit
    @PutMapping("addresses/{id}/default")
    public Object setDefaultAddress(@LoginUser Long userId,
                                    @LoginName String userName,
                                    @PathVariable Long id){
        return Common.decorateReturnObject(addressService.setDefaultAddress(userId,userName,id));
    }

    /**
     * 买家修改地址
     */
    @ApiOperation(value = "买家修改地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addAddressVo", value = "地址信息", required = true,dataType = "AddAddressVo", paramType = "body"),
            @ApiImplicitParam(name = "id", value = "地址id", required = true,dataType = "Long", paramType = "path")
    })
    @Audit
    @PutMapping("addresses/{id}")
    public Object changeAddressInfo(@LoginUser Long userId,
                                    @LoginName String userName,
                                    @PathVariable Long id,
                                    @RequestBody AddAddressVo addAddressVo){
        return Common.decorateReturnObject(addressService.changeAddressInfo(userId,userName,id,addAddressVo));
    }

    /**
     * 买家删除地址
     */
    @ApiOperation(value = "买家删除地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "地址id", required = true,dataType = "Long", paramType = "path")
    })
    @Audit
    @DeleteMapping("addresses/{id}")
    public Object delAddress(@LoginUser Long userId,
                             @LoginName String userName,
                             @PathVariable Long id){
        return Common.decorateReturnObject(addressService.delAddress(userId,id));
    }
}
