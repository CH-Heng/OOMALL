package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.bo.OnSaleGetBo;
import cn.edu.xmu.oomall.goods.service.OnSaleGetService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 02:44
 **/
@RestController
@RefreshScope
@RequestMapping(produces = "application/json;charset=UTF-8")
public class OnSaleGetController {

    @Autowired
    private OnSaleGetService onSaleService;

    /**
     * 管理员查询特定商品的价格浮动
     */
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/products/{id}/onsales")
    public Object selectCertainOnsale(@LoginUser Long loginUser, @LoginName String loginUsername,
                                      @PathVariable("shopId")Long shopId, @PathVariable("id")Long id,
                                      @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        ReturnObject returnObject=onSaleService.selectCertainOnsale(shopId,id,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员查询特定价格浮动的详情
     * @return 只返回普通和秒杀，其他类型出403，返回的是完整订单
     */
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/onsales/{id}")
    public Object selectOnsale(@LoginUser Long loginUser,@LoginName String loginUsername,
                               @PathVariable("shopId")Long shopId, @PathVariable("id")Long id){
        ReturnObject returnObject = onSaleService.selectOnsale(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询团购预售活动的所有价格浮动
     */
    @Audit(departName = "shops")
    @GetMapping("internal/shops/{did}/activities/{id}/onsales")
    public Object selectActivities(@LoginUser Long loginUser, @LoginName String loginUsername,
                                   @PathVariable("did")Long did, @PathVariable("id")Long id, @RequestParam(required = false) Byte state,
                                   @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                   @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                   @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state!=null){
            if(state< OnSaleGetBo.State.DRAFT.getCode()||state>OnSaleGetBo.State.OFFLINE.getCode()){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
        }
        LocalDateTime begin=null,end=null;
        if(beginTime!=null){
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endTime!=null){
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(beginTime!=null&&endTime!=null){
            if(beginTime.isAfter(endTime)){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
        }
        ReturnObject returnObject= onSaleService.selectActivities(did,id,state,begin,end,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 内部API-查询特定活动的所有价格浮动
     */
    @Audit(departName = "shops")
    @GetMapping("internal/shops/{did}/shareactivities/{id}/onsales")
    public Object selectShareActivities(@LoginUser Long loginUser,@LoginName String loginUsername,
                                        @PathVariable("did")Long did, @PathVariable("id")Long id,@RequestParam(required = false) Byte state,
                                        @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(state!=null){
            if(state< OnSaleGetBo.State.DRAFT.getCode()||state>OnSaleGetBo.State.OFFLINE.getCode()){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.FIELD_NOTVALID);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
        }
        ReturnObject returnObject = onSaleService.selectShareActivities(did,id,state,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 内部API- 查询特定价格浮动的详情，该方法加入redis
     * @return 所有类型都会返回
     */
    @GetMapping( "internal/onsales/{id}")
    public Object selectFullOnsale(@PathVariable("id")Long id) {
        return onSaleService.selectFullOnsale(id);
    }

    /**
     * 管理员查询所有商品的价格浮动，所有状态、类型都会返回
     */
    @GetMapping("internal/onsales")
    public Object selectAnyOnsale(@LoginUser Long loginUser, @LoginName String loginUsername,
                                  @RequestParam(required = false) Long shopId, @RequestParam(required = false) Long productId,
                                  @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                  @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                  @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        LocalDateTime begin=null,end=null;
        if(beginTime!=null&&endTime!=null){
            if(beginTime.isAfter(endTime)){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject returnObject= onSaleService.selectAnyOnsale(shopId,productId,begin,end,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id onSaleId
     * @return 是否存在
     */
    @GetMapping("/internal/onsale/{id}/exist")
    public Object isOnsaleExist(@PathVariable Long id){
        return Common.decorateReturnObject(onSaleService.isOnsaleExit(id));
    }


}
