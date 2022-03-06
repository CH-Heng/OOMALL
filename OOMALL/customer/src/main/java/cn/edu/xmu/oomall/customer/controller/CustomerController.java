package cn.edu.xmu.oomall.customer.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.oomall.customer.service.CustomerService;
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
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
     * 获得买家的所有状态
     */
    @ApiOperation(value = "获得买家的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("customer/states")
    public Object getUserState() {
        return Common.decorateReturnObject(customerService.getUserState());
    }

    /**
     * 注册用户
     */
    @ApiOperation(value = "注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "createUserVo", value = "用户信息", required = true, dataType = "CreateUserVo", paramType = "body")
    })
    @PostMapping("customers")
    public Object registerUser(@RequestBody CreateUserVo createUserVo,
                               HttpServletResponse httpServletResponse) {
        if(createUserVo.getUserName()==null){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        ReturnObject returnObject=customerService.registerUser(createUserVo);
        if(returnObject.getCode()==ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家查看自己信息
     */
    @ApiOperation(value = "买家查看自己信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "买家用户名", required = true, dataType = "String", paramType = "query")
    })
    @Audit
    @GetMapping("self")
    public Object getSelfInfo(@LoginUser Long userId,
                              @LoginName String userName) {
        return Common.decorateReturnObject(customerService.showOwnCustomerSelf(userId, userName));
    }

    /**
     * 买家修改自己的信息
     */
    @ApiOperation(value = "买家修改自己的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "买家用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "customerself", value = "买家信息", required = true, dataType = "Customerself", paramType = "body")
    })
    @Audit
    @PutMapping("self")
    public Object changeMyselfInfo(@LoginUser Long userId,
                                   @LoginName String userName,
                                   @RequestBody Customerself customerself) {
        return Common.decorateReturnObject(customerService.changeMyself(userId, userName, customerself));
    }

    /**
     * 用户修改密码
     */
    @ApiOperation(value = "用户修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "买家用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "customerselfPassword", value = "买家信息", required = true, dataType = "CustomerselfPassword", paramType = "body")
    })
    @PutMapping("password")
    public Object changePassword(@RequestBody CustomerselfPassword customerselfPassword) {
        ReturnObject returnObject=customerService.changePassword(customerselfPassword);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     *用户重置密码
     */
    @ApiOperation(value = "用户重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reSetPasswordVo", value = "重置密码", required = true, dataType = "ReSetPasswordVo", paramType = "body")
    })
    @PutMapping("password/reset")
    public Object ReSetPassword(@RequestBody ReSetPasswordVo reSetPasswordVo){
        return Common.decorateReturnObject(customerService.reSetPassword(reSetPasswordVo));
    }

    /**
     * 平台管理员获取所有用户列表
     */
    @ApiOperation(value = "平台管理员获取所有用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "电话", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = true, dataType = "Integer", paramType = "query")
    })
    @Audit
    @GetMapping("shops/{id}/customers/all")
    public Object getAllUsers(@LoginUser Long userId,
                              @LoginName String userName,
                              @PathVariable Integer id,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String mobile,
                              @RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Common.decorateReturnObject(customerService.getAllUsers(userId, userName, id, page, pageSize));
    }

    /**
     * 用户名密码登入
     */
    @ApiOperation(value = "用户名密码登入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginVo", value = "用户密码", required = true, dataType = "LoginVo", paramType = "body")
    })
    @PostMapping("login")
    public Object login(@RequestBody LoginVo loginVo, HttpServletResponse httpServletResponse) {
        ReturnObject returnObject=new ReturnObject();
        returnObject = customerService.login(loginVo,httpServletResponse);

        if(returnObject.getCode()==ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }else if(returnObject.getCode()==ReturnNo.CUSTOMER_FORBIDDEN){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }else if(returnObject.getCode()==ReturnNo.FIELD_NOTVALID){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else if(returnObject.getCode()==ReturnNo.CUSTOMER_INVALID_ACCOUNT){
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 用户登出
     */
    @ApiOperation(value = "用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query")
    })
    @Audit
    @GetMapping("logout")
    public Object logout(@LoginUser Long userId,
                         @LoginName String userName) {
        return Common.decorateReturnObject(customerService.logout(userId, userName));
    }

    /**
     * 平台管理员查看任意买家信息
     */
    @ApiOperation(value = "平台管理员查看任意买家信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "shopId", value = "只为0", required = true, dataType = "Integer", paramType = "path")
    })
    @Audit
    @GetMapping("shop/{shopId}/customers/{id}")
    public Object getUserById(@LoginUser Long userId,
                              @LoginName String userName,
                              @PathVariable Long shopId,
                              @PathVariable Long id) {
        return Common.decorateReturnObject(customerService.getUserById(shopId, id));
    }

    /**
     * 平台管理员封禁买家
     */
    @ApiOperation(value = "平台管理员封禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "did", value = "只能为0", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    })
    @Audit
    @PutMapping("shops/{shopId}/customers/{id}/ban")
    public Object banUser(@LoginUser Long userId,
                          @LoginName String userName,
                          @PathVariable Long id,
                          @PathVariable Long shopId) {
        return Common.decorateReturnObject(customerService.banUser(shopId, id, userId, userName));
    }

    /**
     * 平台管理员解禁买家
     */
    @ApiOperation(value = "平台管理员解禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "买家id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "did", value = "只能为0", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    })
    @Audit
    @PutMapping("shops/{did}/customers/{id}/release")
    public Object releaseUser(@LoginUser Long userId,
                              @LoginName String userName,
                              @PathVariable Long id,
                              @PathVariable Long did) {
        return Common.decorateReturnObject(customerService.releaseUser(did, id, userId, userName));
    }
}


