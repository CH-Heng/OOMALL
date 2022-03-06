package cn.edu.xmu.oomall.activity.microservice;

import cn.edu.xmu.oomall.activity.microservice.vo.*;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleModifyVo;
import cn.edu.xmu.oomall.activity.model.vo.OnsaleVo;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Gao Yanfeng
 * @date 2021/11/13
 */
@FeignClient(value = "goods-service")
public interface GoodsService {
    @GetMapping("/internal/onsales")
    InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> getOnSales(@RequestParam(value = "shopId",required = false) Long shopId,
                                                                @RequestParam(value = "productId",required = false)Long productId,
                                                                @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                                                @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX")ZonedDateTime endTime,
                                                                @RequestParam(value = "page",required = false) Integer page,
                                                                @RequestParam(value = "pageSize",required = false) Integer pageSize);

    @PutMapping("/internal/shops/{shopId}/activities/{id}/onsales/online")
    InternalReturnObject onlineOnSale(@PathVariable Long shopId, @PathVariable Long id);

    @PutMapping("/internal/shops/{shopId}/activities/{id}/onsales/offline")
    InternalReturnObject offlineOnsale(@PathVariable Long shopId, @PathVariable Long id);

    @PutMapping("/internal/shops/{did}/onsales/{id}")
    InternalReturnObject modifyOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id")Long onsaleId, @RequestBody OnsaleModifyVo vo);

    @DeleteMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject deleteOnsale(@PathVariable(value="did") Long shopId, @PathVariable(value="id") Long activityId);

    @GetMapping("/internal/shops/{did}/activities/{id}/onsales")
    InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> getShopOnSaleInfo(@PathVariable("did")Long did,
                                                                       @PathVariable("id")Long id,
                                                                       @RequestParam(value = "state",required = false)Byte state,
                                                                       @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                                                       @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                                                       @RequestParam(value = "page",required = false) Integer page,
                                                                       @RequestParam(value = "pageSize",required = false) Integer pageSize);


    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<FullOnSaleVo> getOnSaleById(@PathVariable("id")Long id);

    @PostMapping("/internal/shops/{did}/products/{id}/onsales")
    InternalReturnObject<NewOnSaleRetVo> addOnSale(@PathVariable Long did, @PathVariable Long id,@RequestBody OnSaleCreatedVo onSaleCreatedVo);

    @PutMapping("/shops/{shopId}/onsales/{id}")
    InternalReturnObject modifyOnSaleShareActId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@RequestBody ModifyOnSaleVo onSale);

    @GetMapping("/products/{id}/exist")
    InternalReturnObject<Boolean> existProduct(@PathVariable("id")Long id);

}
