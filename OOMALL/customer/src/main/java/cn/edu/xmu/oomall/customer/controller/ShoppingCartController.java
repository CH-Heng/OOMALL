package cn.edu.xmu.oomall.customer.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.oomall.customer.service.CustomerService;
import cn.edu.xmu.oomall.customer.service.ShoppingCartService;
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
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 买家获得购物车列表
     */
    @ApiOperation(value = "买家将商品加入购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "param"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "param")
    })
    @Audit
    @GetMapping("carts")
    public Object getCart(@LoginUser Long userId,
                          @LoginName String userName,
                          @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) Integer pageSize){
        return Common.decorateReturnObject(shoppingCartService.getCart(userId,page,pageSize));
    }

    /**
     * 买家将商品加入购物车
     */
    @ApiOperation(value = "买家将商品加入购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addCartVo", value = "买家填写信息", required = true, dataType = "AddCartVo", paramType = "body")
    })
    @Audit
    @PostMapping("carts")
    public Object addToCart(@LoginUser Long userId,
                            @LoginName String userName,
                            @RequestBody AddCartVo addCartVo){
        return Common.decorateReturnObject(shoppingCartService.addToCart(userId,userName,addCartVo));
    }

    /**
     * 买家清空购物车
     */
    @ApiOperation(value = "买家清空购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query")
    })
    @Audit
    @DeleteMapping("carts")
    public Object clearGoods(@LoginUser Long userId,
                             @LoginName String userName){
        return Common.decorateReturnObject(shoppingCartService.clearGoods(userId));
    }

    /**
     * 买家修改购物车单个商品的数量或规格
     */
    @ApiOperation(value = "买家修改购物车单个商品的数量或规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "购物车ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "changeCartInfoVo", value = "修改购物车单个商品信息", required = true, dataType = "ChangeCartInfoVo", paramType = "body")
    })
    @Audit
    @PutMapping("cart/{id}")
    public Object changeCartInfo(@LoginUser Long userId,
                                 @LoginName String userName,
                                 @PathVariable Long id,
                                 @RequestBody ChangeCartInfoVo changeCartInfoVo){
        return Common.decorateReturnObject(shoppingCartService.changeCartInfo(userId,id,changeCartInfoVo));
    }

    /**
     *买家删除购物车中商品
     */
    @ApiOperation(value = "买家删除购物车中商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "购物车ID", required = true, dataType = "Long", paramType = "path"),
    })
    @Audit
    @DeleteMapping("cart/{id}")
    public Object delGoods(@LoginUser Long userId,
                           @LoginName String userName,
                           @PathVariable Long id){
        return Common.decorateReturnObject(shoppingCartService.delGoods(userId,id));
    }
}
