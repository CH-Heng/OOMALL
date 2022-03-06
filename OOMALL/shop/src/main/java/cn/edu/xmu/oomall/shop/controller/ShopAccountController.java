package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountVo;
import cn.edu.xmu.oomall.shop.service.ShopAccountService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 * @studentId 34520192201587
 */


@Api(value = "店铺账户API")
@RestController /*Restful的Controller对象*/
@RefreshScope
@RequestMapping(value = "shops", produces = "application/json;charset=UTF-8")
public class ShopAccountController {

    @Autowired
    private ShopAccountService shopAccountService;

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @ApiOperation(value = "管理员增加店铺的账户" ,tags = "shop")
    @Audit(departName = "shops")
    @PostMapping(value = "/{id}/accounts")
    public Object addShopAccount(@ApiParam(value = "店铺id", required = true) @PathVariable("id") Long shopId,
                                 @LoginUser Long loginUserId, @LoginName String loginUserName,
                                 @ApiParam(value = "账户信息", required = true) @Valid @RequestBody ShopAccountVo shopAccountVo,
                                 BindingResult bindingResult, HttpServletResponse httpServletResponse){
        loginUserId = Long.valueOf(1);
        loginUserName = "wangxusheng";
        var res = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(res != null){
            return res;
        }
        ReturnObject ret = shopAccountService.addShopAccount(shopAccountVo, shopId, loginUserId, loginUserName);
        if(ret.getCode().equals(ReturnNo.OK)){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @ApiOperation(value = "管理员获得店铺的账户" ,tags = "shop")
    @Audit(departName = "shops")
    @GetMapping(value = "/{id}/accounts")
    public Object getShopAccounts(@ApiParam(value = "店铺id",required=true) @PathVariable("id") Long shopId,@LoginUser Long loginUserId,@LoginName String loginUserName) {
        ReturnObject ret = shopAccountService.getShopAccounts(shopId);
        return Common.decorateReturnObject(ret);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @ApiOperation(value = "管理员删除店铺的账户" ,tags = "shop")
    @Audit(departName = "shops")
    @DeleteMapping(value = "/{shopId}/accounts/{id}")
    public Object deleteShopAccount(@ApiParam(value = "店铺id",required=true) @PathVariable("shopId") Long shopId,
                                    @ApiParam(value = "店铺账户id",required=true) @PathVariable("id") Long accountId,
                                    @LoginUser Long loginUserId,@LoginName String loginUserName){
        ReturnObject ret = shopAccountService.deleteAccount(shopId,accountId);
        return Common.decorateReturnObject(ret);
    }
}
