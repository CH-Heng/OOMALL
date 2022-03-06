package cn.edu.xmu.oomall.goods.controller;


import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import com.alibaba.druid.sql.visitor.functions.Bin;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.*;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author yujie lin 22920192204242
 * @date 2021/11/10
 */
@Api(value = "货品销售情况", tags = "goods")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class OnSaleController {

    private final Logger logger = LoggerFactory.getLogger(OnSaleController.class);

    @Autowired
    private OnsaleService onsaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "管理员新增商品价格和数量（普通和秒杀）")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 902, message = "商品销售时间冲突"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间"),
            @ApiResponse(code = 505, message = "限定秒杀或普通"),
    })

    @Audit(departName = "shops")
    @PostMapping("shops/{shopId}/products/{id}/onsales")
    public Object createNewOnSaleNormalSeckill(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody NewOnSaleVo newOnSaleVo,
                                               @LoginUser Long loginUserId, @LoginName String loginUserName, BindingResult bindingResult) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        // 判断是否秒杀或普通
        if (!newOnSaleVo.getType().equals(OnSale.Type.NOACTIVITY.getCode().byteValue())
                && !newOnSaleVo.getType().equals(OnSale.Type.SECKILL.getCode().byteValue())) {
            ReturnObject<Object> returnObject2 = new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE, "限定处理普通或秒杀。");
            return decorateReturnObject(returnObject2);
        }

        // 判断开始时间是否比结束时间晚
        if (newOnSaleVo.getBeginTime().isAfter(newOnSaleVo.getEndTime())) {
            return decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME, "开始时间晚于结束时间。"));
        }

        ReturnObject returnObject1 = onsaleService.createOnSale(shopId, id, newOnSaleVo, loginUserId, loginUserName);

        if (returnObject1.getCode() != ReturnNo.OK) {
            return decorateReturnObject(returnObject1);
        }

        httpServletResponse.setStatus(HttpStatus.CREATED.value());

        return decorateReturnObject(getRetVo(returnObject1, NewOnSaleRetVo.class));


    }


    @ApiOperation(value = "管理员上线商品价格浮动，限定普通秒杀")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "浮动价格id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理普通和秒杀"),
            @ApiResponse(code = 507, message = "只有草稿态才能上线"),
    })
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/onsales/{id}/online")
    public Object onlineOnSaleNormalSeckill(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {

        ReturnObject returnObject1 = onsaleService.onlineOrOfflineOnSale(shopId, id, loginUserId, loginUserName, OnSale.State.ONLINE);
        return decorateReturnObject(returnObject1);
    }

    @ApiOperation(value = "管理员下线商品价格浮动，限定普通秒杀")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "浮动价格id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理普通和秒杀"),
            @ApiResponse(code = 507, message = "只有上线态才能下线"),
    })
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/onsales/{id}/offline")
    public Object offlineOnSaleNormalSeckill(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {


        ReturnObject returnObject1 = onsaleService.onlineOrOfflineOnSale(shopId, id, loginUserId, loginUserName, OnSale.State.OFFLINE);
        return decorateReturnObject(returnObject1);
    }


    @ApiOperation(value = "管理员上线团购和预售活动的商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理团购和预售"),
    })
    @Audit
    @PutMapping("internal/shops/{shopId}/activities/{id}/onsales/online")
    public Object onlineOnSaleGroupPre(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {
        ReturnObject returnObject1 = onsaleService.onlineOrOfflineOnSaleGroupPre(id, loginUserId, loginUserName, OnSale.State.DRAFT, OnSale.State.ONLINE);
        return decorateReturnObject(returnObject1);
    }

    @ApiOperation(value = "管理员下线团购和预售活动的商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理团购和预售"),
    })
    @Audit
    @PutMapping("internal/shops/{shopId}/activities/{id}/onsales/offline")
    public Object offlineOnSaleGroupPre(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {

        ReturnObject returnObject1 = onsaleService.onlineOrOfflineOnSaleGroupPre(id, loginUserId, loginUserName, OnSale.State.ONLINE, OnSale.State.OFFLINE);
        return decorateReturnObject(returnObject1);
    }

    @ApiOperation(value = "管理员新增商品价格和数量")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 902, message = "商品销售时间冲突"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间"),
    })
    @Audit(departName = "shops")
    @PostMapping("internal/shops/{did}/products/{id}/onsales")
    public Object createNewOnSale(@PathVariable("did") Long did, @PathVariable("id") Long id,@Validated @RequestBody NewOnSaleAllVo newOnSaleAllVo,
                                  BindingResult bindingResult,@LoginUser Long loginUserId, @LoginName String loginUserName) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        //判断开始时间是否比结束时间晚
        if (newOnSaleAllVo.getBeginTime().isAfter(newOnSaleAllVo.getEndTime())) {
            return decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME, "开始时间晚于结束时间。"));
        }

        ReturnObject returnObject1 = onsaleService.createOnSaleAll(did, id, newOnSaleAllVo, loginUserId, loginUserName);
        if (!returnObject1.getCode().equals( ReturnNo.OK)) {
            return decorateReturnObject(returnObject1);
        }
        httpServletResponse.setStatus(HttpStatus.CREATED.value());

        return decorateReturnObject(getRetVo(returnObject1, NewOnSaleRetVo.class));
    }


    @ApiOperation(value = "物理删除草稿态的普通和秒杀")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "销售id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理普通和秒杀"),
            @ApiResponse(code = 507, message = "只能删除草稿态"),
    })
    @Audit(departName = "shops")
    @DeleteMapping("shops/{shopId}/onsales/{id}")
    public Object deleteOnSaleNorSec(@PathVariable Long shopId, @PathVariable Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {


        ReturnObject returnObject1 = onsaleService.deleteOnSaleNorSec(shopId, id);
        return decorateReturnObject(returnObject1);
    }

    @ApiOperation(value = "物理删除草稿态的团购和预售")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "销售id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "限定只能处理团购和预售"),
            @ApiResponse(code = 507, message = "只能删除草稿态"),
    })
    @Audit(departName = "shops")
    @DeleteMapping("internal/shops/{did}/activities/{id}/onsales")
    public Object deleteOnSaleGroPre(@PathVariable Long did, @PathVariable Long id, @LoginUser Long loginUserId, @LoginName String loginUserName) {


        ReturnObject returnObject1 = onsaleService.deleteOnSaleGroPre(id);
        return decorateReturnObject(returnObject1);
    }


    @ApiOperation(value = "修改任意类型商品价格和数量")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "只有草稿态和下线态才能修改"),
    })
    @Audit(departName = "shops")
    @PutMapping("internal/shops/{did}/onsales/{id}")
    public Object modifyOnSale(@PathVariable Long did,@PathVariable Long id,@Validated @RequestBody ModifyOnSaleVo onSale,
                               @LoginUser Long loginUserId, @LoginName String loginUserName, BindingResult bindingResult) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(onSale.getBeginTime()!=null&&onSale.getEndTime()!=null) {
            // 判断开始时间是否比结束时间晚
            if (onSale.getBeginTime().isAfter(onSale.getEndTime())) {
                return decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME, "开始时间晚于结束时间。"));
            }
        }
        ReturnObject returnObject1 = onsaleService.updateOnSale(id, onSale,loginUserId, loginUserName);
        return decorateReturnObject(returnObject1);
    }


    @ApiOperation(value = "修改普通和秒杀价格和数量")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "只有草稿态和下线态才能修改"),
    })
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/onsales/{id}")
    public Object modifyOnSaleNorSec(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody ModifyOnSaleVo onSale, @LoginUser Long loginUserId, @LoginName String loginUserName,
                                     BindingResult bindingResult) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        // 判断开始时间是否比结束时间晚
        if(onSale.getEndTime()!=null&&onSale.getBeginTime()!=null){
            if (onSale.getBeginTime().isAfter(onSale.getEndTime())) {
                return decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME, "开始时间晚于结束时间。"));
            }
        }

        OnSale bo = cloneVo(onSale, OnSale.class);
        bo.setId(id);

        ReturnObject returnObject1 = onsaleService.updateOnSaleNorSec(bo, shopId, loginUserId, loginUserName);
        return decorateReturnObject(returnObject1);
    }


    @Audit(departName = "shops")
    @PutMapping("internal/shops/{did}/onsales/{id}/decr")
    public Object decreaseOnSale(@PathVariable Long did, @PathVariable Long id,@Validated @RequestBody QuantityVo vo,
                                 @LoginUser Long loginUserId,
                                 @LoginName String loginUserName, BindingResult bindingResult) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        Integer quantity = vo.getQuantity();
        return decorateReturnObject(onsaleService.decreaseOnSale(did, id, quantity, loginUserId, loginUserName));
    }


    @Audit(departName = "shops")
    @PutMapping("internal/shops/{did}/onsales/{id}/incr")
    public Object increaseOnSale(@PathVariable Long did, @PathVariable Long id,@Validated @RequestBody QuantityVo vo,
                                 @LoginUser Long loginUserId,
                                 @LoginName String loginUserName, BindingResult bindingResult) {

        Object returnObject = processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        Integer quantity = vo.getQuantity();
        return decorateReturnObject(onsaleService.increaseOnSale(did, id, quantity, loginUserId, loginUserName));
    }


}
