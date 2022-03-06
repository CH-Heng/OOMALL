package cn.edu.xmu.oomall.aftersale.controller;

import cn.edu.xmu.oomall.aftersale.service.AftersaleService;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import cn.edu.xmu.oomall.aftersale.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Api(value = "售后服务", tags = "aftersale")
@RestController /*Restful的Controller对象*/
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
@Component
public class AftersaleController {
    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private AftersaleService aftersaleService;

    /**
     * 获得售后单的所有状态
     * @author wxt
    */
    @ApiOperation(value="获得售后单的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping(value = "/aftersales/states")
    public Object getStates() {
        ReturnObject retObj = aftersaleService.getStates();
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 买家查询所有的售后单信息
     * @author wxt
     */
    @ApiOperation(value= "买家查询所有的售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户Token",
                    required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间"),
            @ApiImplicitParam(paramType = "query", dataType="Integer",  name="state",value="售后状态"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer",name = "page", value = "页码"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer",name = "pageSize", value = "每页数目"),
    })
    @ApiResponses({
         @ApiResponse(code=0,message = "成功"),
    })
    @Audit
    @GetMapping(value="/aftersales")
    public Object getAllAftersales(
            @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginTime,
            @RequestParam(required = false)@Min(0) @Max(7) Integer state,
            @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endTime,
            @RequestParam(required = false, defaultValue = "1")@Min(1) @NotNull Integer page,
            @RequestParam(required = false, defaultValue = "10")@Min(1) @NotNull Integer pageSize,
            @LoginUser Long userId){
        //输入参数合法性检查
        if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        if(state!=null) {
            if (state < 0 || state > 7) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "输入的state不合法"));
            }
        }
      ReturnObject ret=aftersaleService.getAllAftersalesByUser(userId,state,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员查询所有的售后单
     * @author wxt
     */
    @ApiOperation(value = "管理员查询所有的售后单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value = "用户token",required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name="id",              value="店铺id",required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",     value = "开始时间"),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",       value = "结束时间"),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value = "页码"),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value = "每页数目"),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "type",          value = "售后类型"),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "state",         value = "售后状态")
    })
    @ApiResponses({
            @ApiResponse(code = 0,   message = "成功")
    })
    @Audit(departName="shops")
    @GetMapping("/shops/{id}/aftersales")
    public Object adminGetAllAftersale(
            @LoginUser Long userId,
            @Depart Long did,
            @PathVariable("id") Long shopId,
            @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginTime,
            @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endTime,
            @RequestParam(required = false, defaultValue = "1") @Min(1) @NotNull Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @NotNull Integer pageSize,
            @RequestParam(required = false) @Min(0)@Max(2) Integer type,
            @RequestParam(required = false) @Min(0)@Max(8) Integer state) {
          if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
          }
        if(state!=null) {
            if (state < 0 || state > 8 ) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "输入的state不合法"));
            }
        }
        if(type!=null) {
            if (type < 0 || type > 2 ) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "输入的type不合法"));
            }
        }
        ReturnObject ret=aftersaleService.getAllAftersalesByAdmin(shopId,state,type,beginTime,endTime,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 买家根据售后单id查询售后单信息
     * @author wxt
     */
    @ApiOperation(value= "买家根据售后单id查询售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户Token",
                    required = true),
            @ApiImplicitParam(paramType = "path",dataType="String",name="id",value="售后单id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
    })
    @Audit
    @GetMapping(value="/aftersales/{id}")
    public Object getAftersalesById(
            @LoginUser Long userId, @PathVariable("id") Long aftersaleId){
        ReturnObject ret=aftersaleService.getAftersalesByUserId(userId,aftersaleId);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员根据售后单id查询售后单信息
     * @author wxt
     */
    @ApiOperation(value="管理员根据售后单id查询售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户Token",
                    required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name="shopId",value="店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType="Integer",name="id",value="售后单id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
    })
    @Audit(departName="shops")
    @GetMapping(value="/shops/{shopId}/aftersales/{id}")
    public Object adminGetAftersalesById(
            @LoginUser Long userId, @Depart Long did,
            @PathVariable("shopId") Long shopId, @PathVariable("id") Long id){
        ReturnObject ret=aftersaleService.getAftersalesByAdminId(shopId,id);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 获得售后单的支付信息
     * @author wxt
     */
    @ApiOperation(value="获得售后单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户Token",
                    required = true),
            @ApiImplicitParam(paramType = "path",dataType="Integer",name="id",value="售后id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
    })
    @Audit(departName="shops")
    @GetMapping(value="/aftersales/{id}/payments")
    public Object getPaymentsById(
            @LoginUser Long userId, @Depart Long did,
           @PathVariable("id") Long id) {
        ReturnObject ret = aftersaleService.getPaymentById(id);
        return Common.decorateReturnObject(ret);
    }


    /** 买家提交售后单
     * @Param id 订单明细id
     * @author 张晖婧
     */
    @ApiOperation(value = "买家提交售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "订单明细id"),
            @ApiImplicitParam(paramType = "body", dataType = "NewAftersaleVo", name = "newAftersaleVo", value = "新增售后单", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/orderitems/{id}/aftersales")
    public Object submitAftersale(
            @PathVariable("id") Long id,
            @Validated @RequestBody NewAftersaleVo newAftersaleVo,
            BindingResult bindingResult, @LoginUser Long loginUser, @LoginName String loginUserName){

        //校验前端数据售后单不能为空
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.submitAftersale(id, newAftersaleVo, loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 买家修改售后单信息（店家发货前）
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "买家修改售后单信息（店家发货前）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id"),
            @ApiImplicitParam(paramType = "body", dataType = "UpdateAftersaleVo", name = "updateAftersaleVo", value = "售后单修改信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/aftersales/{id}")
    public Object updateAftersale(
            @PathVariable("id") Long id,
            @Validated @RequestBody UpdateAftersaleVo updateAftersaleVo,
            BindingResult bindingResult, @LoginUser Long loginUser, @LoginName String loginUserName){

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.updateAftersale(id, updateAftersaleVo, loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 买家取消售后单和逻辑删除售后单
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "买家取消售后单和逻辑删除售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/aftersales/{id}")
    public Object deleteAftersale(
            @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUserName){

        ReturnObject retObj = aftersaleService.deleteAftersale(id,loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 买家填写售后单的运单信息
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "买家填写售后单的运单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id"),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleLogSnVo", name = "aftersaleLogSnVo", value = "售后单运单信息（顾客）", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/aftersales/{id}/sendback")
    public Object fillCustomerLogSn(
            @PathVariable("id") Long id,
            @Validated @RequestBody AftersaleLogSnVo aftersaleLogSnVo,
            BindingResult bindingResult, @LoginUser Long loginUser, @LoginName String loginUserName){

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.fillCustomerLogSn(id,aftersaleLogSnVo,loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 买家确认售后单结束
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "买家确认售后单结束")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/aftersales/{id}/confirm")
    public Object confirmAftersaleByCustomer(
            @Min(value = 0,message = "大于0") @PathVariable("id") Long id , @LoginUser Long loginUser, @LoginName String loginUserName){

        ReturnObject retObj = aftersaleService.confirmAftersaleByCustomer(id,loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 管理员同意/不同意（退款，换货，维修）
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "管理员同意/不同意（退款，换货，维修）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id"),
            @ApiImplicitParam(paramType = "body", dataType = "AdminConclusionVo", name = "adminConclusionVo", value = "管理员处理意见", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    public Object confirmAftersaleByAdmin(@PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,@Validated @RequestBody AdminConclusionVo adminConclusionVo,
                                          BindingResult bindingResult,
                                          @LoginUser Long loginUser, @LoginName String loginUserName){
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.confirmAftersaleByAdmin(shopId,id,adminConclusionVo,loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 店家验收收到买家的退（换）货
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "店家确认收到买家的退（换）货")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id"),
            @ApiImplicitParam(paramType = "body", dataType = "ShopConclusionVo", name = "shopConclusionVo", value = "店铺处理意见", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    public Object confirmRecieveByShop(@PathVariable("shopId")Long shopId,
                                          @PathVariable("id") Long id,@Validated @RequestBody ShopConclusionVo shopConclusionVo,
                                          BindingResult bindingResult,
                                          @LoginUser Long loginUser, @LoginName String loginUserName){
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.confirmRecieveByShop(shopId,id,shopConclusionVo,loginUser,loginUserName);
        return Common.decorateReturnObject(retObj);
    }

    /** 店家寄出货物
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    @ApiOperation(value = "店家寄出货物")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path",value = "售后单id"),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleShopLogSnVo", name = "aftersaleShopLogSnVo", value = "售后单运单信息（店铺）", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    public Object deliverAgain(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody AftersaleShopLogSnVo aftersaleShopLogSnVo,
            BindingResult bindingResult, @LoginUser Long loginUser, @LoginName String loginUserName) {

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        ReturnObject retObj = aftersaleService.deliverAgain(shopId, id, aftersaleShopLogSnVo, loginUser, loginUserName);
        return Common.decorateReturnObject(retObj);
    }
    /* ------------------------------ 内部 API ------------------------------ */
    /**
     * 根据sn获取售后单
     * @author wxt
     */
    @ApiOperation("根据sn获取售后单信息")
    @GetMapping("/internal/aftersale")
    public Object getAftersaleBySn(@RequestParam String aftersaleSn)
    {
        ReturnObject retObj=aftersaleService.getAftersalesBySn(aftersaleSn);
        return Common.decorateReturnObject(retObj);
    }

}
