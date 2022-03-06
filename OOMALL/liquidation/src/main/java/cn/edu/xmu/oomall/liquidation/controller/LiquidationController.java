package cn.edu.xmu.oomall.liquidation.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.liquidation.model.bo.ExpenditureItem;
import cn.edu.xmu.oomall.liquidation.model.bo.Liquidation;
import cn.edu.xmu.oomall.liquidation.model.bo.RevenueItem;
import cn.edu.xmu.oomall.liquidation.model.vo.StartInfoVo;
import cn.edu.xmu.oomall.liquidation.service.LiquidationService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author wxt
 * @author zhj
 * @date 2021/12/15
 */
@Api(value = "清算", tags = "liquidation")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class LiquidationController {

    @Autowired
    private LiquidationService liquidationService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @GetMapping(value = "/liquidation/states")
    public Object getStates() {
        ReturnObject retObj = liquidationService.getStates();
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "平台管理员或商家获取符合条件的清算单简单信息")
    @Audit(departName = "departs")
    @GetMapping(value = "/shops/{shopId}/liquidation")
    public Object getSimpleLiquidation(@LoginUser Long loginId,
                                       @LoginName String loginUserName,
                                       @Depart Long departId,
                                       @PathVariable Long shopId,
                                       @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginDate,
                                       @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endDate,
                                       @RequestParam(required = false) Boolean state,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer pageSize) {
        Liquidation liquidation = new Liquidation();
        liquidation.setShopId(shopId);
        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;
        if(beginDate!=null) {
            beginTime = beginDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endDate!=null) {
            endTime = endDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        liquidation.setState(state ? Liquidation.State.REMITTED.getCode() : Liquidation.State.NOT_REMITTED.getCode());
        ReturnObject retObj = liquidationService.getSimpleLiquidation(liquidation, beginTime, endTime, page, pageSize);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "查询指定清算单详情")
    @Audit(departName = "departs")
    @GetMapping(value = "/shops/{shopId}/liquidation/{id}")
    public Object getDetailLiquidation(@LoginUser Long loginId,
                                       @LoginName String loginUserName,
                                       @Depart Long departId,
                                       @PathVariable Long shopId,
                                       @PathVariable Long id) {
        ReturnObject retObj = liquidationService.getLiquidationInfo(shopId, id);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "管理员按条件查某笔的进账")
    @Audit(departName = "departs")
    @GetMapping("/shops/{shopId}/revenue")
    public Object getRevenue(@LoginUser Long loginId,
                             @LoginName String loginUserName,
                             @Depart Long departId,
                             @PathVariable Long shopId,
                             @RequestParam(required = false) Long orderId,
                             @RequestParam(required = false) Long productId,
                             @RequestParam(required = false) Integer page,
                             @RequestParam(required = false) Integer pageSize) {
        RevenueItem revenueItem = new RevenueItem();
        revenueItem.setShopId(shopId);
        revenueItem.setOrderId(orderId);
        revenueItem.setProductId(productId);
        ReturnObject retObj = liquidationService.getRevenue(revenueItem, page, pageSize);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "管理员按条件查对应清算单的出账")
    @Audit(departName = "departs")
    @GetMapping("/shops/{shopId}/expenditure")
    public Object getExpenditure(@LoginUser Long loginId,
                                 @LoginName String loginUserName,
                                 @Depart Long departId,
                                 @PathVariable Long shopId,
                                 @RequestParam(required = false) Long orderId,
                                 @RequestParam(required = false) Long productId,
                                 @RequestParam(required = false) Long liquidationId,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer pageSize) {
        ExpenditureItem expenditureItem = new ExpenditureItem();
        expenditureItem.setShopId(shopId);
        expenditureItem.setOrderId(orderId);
        expenditureItem.setProductId(productId);
        expenditureItem.setLiquidId(liquidationId);
        ReturnObject retObj = liquidationService.getExpenditure(expenditureItem, page, pageSize);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "管理员按id查出账对应的进账")
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/expenditure/{id}/revenue")
    public Object getRevenueByExpenditure(@LoginUser Long loginId,
                                          @LoginName String loginUserName,
                                          @Depart Long departId,
                                          @PathVariable Long shopId,
                                          @PathVariable Long id) {
        ReturnObject retObj = liquidationService.getRevenueByExpenditureId(shopId, id);
        return Common.decorateReturnObject(retObj);
    }


    @ApiOperation(value = "用户获取自己因分享得到返点的记录")
    @Audit
    @GetMapping("/pointrecords/revenue")
    public Object getPointRecordRevenues(@LoginUser Long loginId,
                                         @LoginName String loginUserName,
                                         @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginDate,
                                         @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endDate,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer pageSize) {
        RevenueItem revenueItem = new RevenueItem();
        revenueItem.setSharerId(loginId);
        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;
        if(beginDate!=null) {
            beginTime = beginDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endDate!=null) {
            endTime = endDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject retObj = liquidationService.getUserRevenue(revenueItem, beginTime, endTime, page, pageSize);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "用户获取自己因分享得到返点的记录")
    @Audit
    @GetMapping("/pointrecords/expenditure")
    public Object getPointRecordsExpenditure(@LoginUser Long loginId,
                                             @LoginName String loginUserName,
                                             @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginDate,
                                             @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endDate,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer pageSize) {
        ExpenditureItem expenditureItem = new ExpenditureItem();
        expenditureItem.setSharerId(loginId);
        LocalDateTime beginTime = null;
        LocalDateTime endTime = null;
        if(beginDate!=null) {
            beginTime = beginDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endDate!=null) {
            endTime = endDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject retObj = liquidationService.getUserExpenditure(expenditureItem, beginTime, endTime, page, pageSize);
        return Common.decorateReturnObject(retObj);
    }

    @ApiOperation(value = "开始清算")
    @Audit(departName = "departs")
    @PutMapping("/shops/{shopId}/liquidation/start")
    public Object startLiquidation(@LoginUser Long loginId,
                                   @LoginName String loginUserName,
                                   @Depart Long departId,
                                   @PathVariable Long shopId,
                                   @Validated @RequestBody StartInfoVo startInfo,
                                   BindingResult bindingResult) {
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != obj) {
            return obj;
        }

        if (shopId != 0L) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }

        ReturnObject retObj = liquidationService.startLiquidation(loginId, loginUserName, startInfo.getBeginTime(), startInfo.getEndTime());
        return Common.decorateReturnObject(retObj);
    }
}
