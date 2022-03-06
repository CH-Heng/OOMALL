package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.model.bo.ShareActivity;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityStatesBo;
import cn.edu.xmu.oomall.activity.model.vo.ShareActivityRetVo;
import cn.edu.xmu.oomall.activity.model.vo.ShareActivitySimpleRetVo;
import cn.edu.xmu.oomall.activity.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.activity.service.ShareActivityService;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:52
 */
@Api(value = "分享活动API", tags = "activity")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class ShareActivityController {

    @Autowired
    private ShareActivityService shareActivityService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 获得分享活动的所有状态
     *
     * @return
     */
    @ApiOperation(value = "获得分享活动的所有状态")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @GetMapping("/shareactivities/states")
    public Object getShareState() {
        ReturnObject returnObject = shareActivityService.getShareState();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 显示所有状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品id
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param state     分享活动状态
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @ApiOperation(value = "显示所有状态的分享活动")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "productId", value = "货品id", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态", required = false, dataType = "Byte", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/shareactivities")
    public Object getShareByShopId(@PathVariable(name = "shopId", required = true) Long shopId,
                                   @RequestParam(name = "productId", required = false) Long productId,
                                   @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                   @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                   @RequestParam(name = "state", required = false) Byte state,
                                   @RequestParam(name = "page", required = false) Integer page,
                                   @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (shopId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "shopId错误"));
        }
        if (productId != null && productId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "productId错误"));
        }
        LocalDateTime begin=null;
        LocalDateTime end = null;
        if(beginTime!=null&&endTime!=null){
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject shareByShopId = shareActivityService.getShareByShopId(shopId, productId,
                begin, end, state, page, pageSize);
        return Common.decorateReturnObject(shareByShopId);
    }

    /**
     * 管理员新增分享活动
     *
     * @param shopId          店铺id
     * @param shareActivityVo 可修改的信息
     * @param bindingResult   合法性检验结果
     * @return
     */
    @ApiOperation(value = "管理员新增分享活动")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "shareActivityVo", paramType = "body", dataType = "ShareActivityVo", value = "修改内容", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @PostMapping("/shops/{shopId}/shareactivities")
    @Audit(departName = "shops")
    public Object addShareAct(@LoginName String createName, @LoginUser Long createId, @PathVariable(value = "shopId", required = true) Long shopId,
                              @Validated @RequestBody ShareActivityVo shareActivityVo,
                              BindingResult bindingResult) {
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != obj) {
            return obj;
        }
        if (shareActivityVo.getBeginTime().isAfter(shareActivityVo.getEndTime())) {
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME, "开始时间不得早于结束时间"));
        }
        ReturnObject returnObject = shareActivityService.addShareAct(createName, createId, shopId, shareActivityVo);

        if (returnObject.getCode().equals(ReturnNo.OK)){
            returnObject = new ReturnObject(cloneVo(returnObject.getData(), ShareActivitySimpleRetVo.class));
        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询分享活动 只显示上线状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品id
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @ApiOperation(value = "查询分享活动 只显示上线状态的分享活动")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "productId", value = "货品id", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 503, message = "shopId错误"),
            @ApiResponse(code = 503, message = "页数和页数大小应大于0")
    })
    @Audit
    @GetMapping("/shareactivities")
    public Object getShareActivity(@RequestParam(name = "shopId", required = false) Long shopId,
                                   @RequestParam(name = "productId", required = false) Long productId,
                                   @RequestParam(name = "beginTime", required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                   @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                   @RequestParam(name = "page", required = false) Integer page,
                                   @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (shopId != null && shopId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "shopId错误"));
        }
        if (productId != null && productId <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "productId错误"));
        }
        LocalDateTime begin=null;
        LocalDateTime end = null;
        if(beginTime!=null&&endTime!=null){
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject shareByShopId = shareActivityService.getShareByShopId(shopId, productId,
                begin, end, ShareActivityStatesBo.ONLINE.getCode(), page, pageSize);
        return Common.decorateReturnObject(shareByShopId);
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    @ApiOperation(value = "查询分享活动 只显示上线状态的分享活动")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "分享活动Id", required = true, dataType = "Long", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @Audit
    @GetMapping("/shareactivities/{id}")
    public Object getShareActivityById(@PathVariable(value = "id", required = true) Long id) {
        return Common.decorateReturnObject(shareActivityService.getShareActivityById(id));
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    @ApiOperation(value = "查看特定分享活动详情,显示所有状态的分享活动")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商铺Id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "分享活动Id", required = true, dataType = "Long", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功")})
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/shareactivities/{id}")
    public Object getShareActivityByShopIdAndId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        ReturnObject ret = shareActivityService.getShareActivityByShopIdAndId(shopId, id);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "在已有销售上增加分享")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "在售商品id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "sid", value = "分享活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @PostMapping("/shops/{shopId}/onsales/{pid}/shareactivities/{id}")
    public Object addShareActivityOnOnSale(@PathVariable("shopId") Long shopId, @PathVariable("pid") Long pid, @PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
        ReturnObject ret = shareActivityService.addShareActivityOnOnSale(shopId,pid,id,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "取消已有销售上的分享")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "在售商品id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "sid", value = "分享活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/onSale/{id}/shareActivities/{sid}")
    public Object deleteShareActivityOnOnSale(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @PathVariable("sid") Long sid,@LoginUser Long loginUser,@LoginName String loginUsername){
        ReturnObject ret =shareActivityService.deleteShareActivityOnOnSale(shopId,id,sid,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "修改平台分享活动的内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    public Object modifyShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @RequestBody ShareActivityVo shareActivityVo,@LoginUser Long loginUser,@LoginName String loginUsername){
        if (shareActivityVo.getBeginTime()!=null&&shareActivityVo.getEndTime()!=null){
            if(shareActivityVo.getBeginTime().compareTo(shareActivityVo.getEndTime())>0){
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
            }
        }
        ReturnObject ret = shareActivityService.modifyShareActivity(id, shareActivityVo,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "删除草稿状态的分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/shareactivities/{id}")
    public Object deleteShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
        ReturnObject ret =shareActivityService.deleteShareActivity(id,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "上线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}/online")
    public Object onlineShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
        ReturnObject ret =shareActivityService.onlineShareActivity(id,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "下线分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "分享活动id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/shareactivities/{id}/offline")
    public Object offlineShareActivity(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
        ReturnObject ret =shareActivityService.offlineShareActivity(id,loginUser,loginUsername);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 按Id获取分享活动
     * @param id
     * @return
     */
    @GetMapping("/internal/shareactivities/{id}")
    public Object getShareActivityByIdNoLogin(@PathVariable("id") Long id){
        ReturnObject ret =shareActivityService.getValidShareActivity(id);
        if(ret.getCode()!=ReturnNo.OK||ret.getData()==null){
            return new InternalReturnObject(404,"资源不存在");
        }
        ShareActivitySimpleRetVo vo = cloneVo(ret.getData(),ShareActivitySimpleRetVo.class);
        return new InternalReturnObject(vo);
    }

}
