package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.model.vo.GroupOnActivityVo;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleNewVo;
import cn.edu.xmu.oomall.activity.service.GroupOnActivityService;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Api(value = "团购API", tags = "团购API")
@RestController
@RefreshScope
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
/**
 * @author Lin Jiyuan
 * @sn 30320192200032
 */
public class GroupActivityController {


    @Autowired
    private GroupOnActivityService groupOnActivityService;

    @Autowired
    private HttpServletResponse httpServletResponse;



    @ApiOperation(value = "管理员新增参与团购的商品销售",  produces="application/json")
    @ApiImplicitParam(name = "authorization", value = "activityToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
            @ApiResponse(code = 902, message = "商品销售时间冲突")})
    @Audit(departName = "shops")
    @PostMapping(value="/shops/{shopId}/products/{pid}/groupons/{id}/onsales")
    public Object addGrouponProduct(@PathVariable("shopId") Long shopId, @PathVariable("pid") Long pid,
                                    @PathVariable("id") Long id, @LoginUser Long loginUser, @LoginName String loginUsername,
                                    @Validated @RequestBody OnsaleNewVo vo, BindingResult bindingResult)
    {
        ReturnObject<Object> returnObject = groupOnActivityService.addOnsaleToGroupOn(shopId,pid,id,vo,loginUser,loginUsername);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "管理员物理删除团购活动",  produces="application/json")
    @ApiImplicitParam(name = "authorization", value = "activityToken", required = true, dataType = "String", paramType = "header")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="团购活动对象id" ,required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作")})
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/groupons/{id}")
    public Object delGroupon(@PathVariable("id") Long id,@PathVariable("shopId") Long shopId) {
//        if(shopId!=0){
//            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"非管理员无权操作"));        }
        ReturnObject<Object> returnObject = groupOnActivityService.delGroupon(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "管理员修改团购活动",  produces="application/json")
    @ApiImplicitParam(name = "authorization", value = "activityToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 902, message = "商品销售时间冲突")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/groupons/{id}")
    public Object modiGood(@Validated @RequestBody GroupOnActivityVo groupOnActivityVo, BindingResult bindingResult,@PathVariable("id") Long id, @PathVariable("shopId") Long shopId,@LoginUser Long loginUser,@LoginName String loginUsername){
//        if(shopId!=0){
//            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"非管理员无权操作"));        }
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        if(groupOnActivityVo.getBeginTime()!=null&&groupOnActivityVo.getEndTime()!=null)
        {
            if(groupOnActivityVo.getBeginTime().isAfter(groupOnActivityVo.getEndTime()))
            {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
            }
        }
        ReturnObject ret = groupOnActivityService.modifyGroupon(id, groupOnActivityVo, shopId, loginUser, loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "上线团购活动",  produces="application/json")
    @ApiImplicitParam(name = "authorization", value = "activityToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/groupons/{id}/online")
    public Object onlineGroupOnActivity( @PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
//        if(shopId!=0){
//            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"非管理员无权操作"));        }
        ReturnObject<Object> returnObject = groupOnActivityService.onlineGroupOnActivity( id, shopId,loginUser,loginUsername);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "下线团购活动",  produces="application/json")
    @ApiImplicitParam(name = "authorization", value = "activityToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/groupons/{id}/offline")
    public Object offlineGroupOnActivity( @PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
//        if(shopId!=0){
//            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"非管理员无权操作"));        }
        ReturnObject<Object> returnObject = groupOnActivityService.offlineGroupOnActivity( id, shopId,loginUser,loginUsername);
        return Common.decorateReturnObject(returnObject);
    }


}

