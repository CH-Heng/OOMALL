package cn.edu.xmu.oomall.share.controller;

import cn.edu.xmu.oomall.share.service.ShareService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import cn.edu.xmu.oomall.core.util.Common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static cn.edu.xmu.oomall.share.constant.Constants.DATE_TIME_FORMAT;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Api(value = "分享", tags = "分享")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class ShareController {

    @Autowired
    ShareService shareService;


    /**
     * 分享者分享并生成链接
     * 根据买家id和商品id生成唯一的分享链接
     * @param onsaleId
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @PostMapping("onsales/{id}/shares")
    @Audit
    public Object share(@PathVariable("id") Long onsaleId,@LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        ReturnObject ret = shareService.share(onsaleId,loginUserId,loginUserName);
        return Common.decorateReturnObject(ret);
    }

    /**
     *买家查询分享记录
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("shares")
    @Audit
    public Object getShares(@RequestParam(name = "productId", required = false) Long productId,
                            @RequestParam(name = "beginTime", required = false)@DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime beginTime,
                            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime endTime,
                            @RequestParam(name = "page", required = false) Integer page,
                            @RequestParam(name = "pageSize", required = false) Integer pageSize,
                            @LoginUser Long loginUserId
    ) {
        LocalDateTime localBeginTime=null;
        LocalDateTime localEndTime=null;
        if (beginTime!=null){
            localBeginTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if (endTime!=null){
            localEndTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject retVoObject =
                shareService.getShares(null,productId, localBeginTime, localEndTime,loginUserId, page, pageSize);
        return Common.decorateReturnObject(retVoObject);
    }

    /**
     * 点击分享链接查看商品的详细信息
     * @param shareId
     * @param productId
     * @return
     */
    @GetMapping("shares/{sid}/products/{id}")
    @Audit
    public Object getShareProduct(@PathVariable("sid") Long shareId,
                                  @PathVariable("id") Long productId,
                                  @LoginUser Long loginUserId,
                                  @LoginName String loginUserName) {
        ReturnObject retVoObject =
                shareService.getShareProduct(shareId,productId,loginUserId,loginUserName);
        return Common.decorateReturnObject(retVoObject);
    }

    /**
     * 商铺管理员查询本店商铺的商品分享记录
     * @param shopId
     * @param productId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("shops/{did}/products/{id}/shares")
    @Audit(departName = "shops")
    public Object getProductShares(
                            @PathVariable(name = "did") Long shopId,
                            @PathVariable(name = "id") Long productId,
                            @RequestParam(name = "page", required = false) Integer page,
                            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        ReturnObject retVoObject =
                shareService.getShares(shopId, productId,null,null,null, page, pageSize);
        return Common.decorateReturnObject(retVoObject);
    }

    /**
     * 分享者查询所有分享成功记录
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @param loginUserId
     * @return
     */
    @GetMapping("beshared")
    @Audit
    public Object getSuccessfulShares(
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "beginTime", required = false)@DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime beginTime,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime endTime,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @LoginUser Long loginUserId) {
        LocalDateTime localBeginTime=null;
        LocalDateTime localEndTime=null;
        if (beginTime!=null){
            localBeginTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if (endTime!=null){
            localEndTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject retVoObject =
                shareService.getSuccessfulShares(productId, localBeginTime,localEndTime,page,pageSize, loginUserId);
        return Common.decorateReturnObject(retVoObject);
    }

    /**
     * 店铺管理员查询商品的分享成功记录
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("shops/{did}/products/{id}/beshared")
    @Audit(departName = "shops")
    public Object getProductSuccessfulShares(
            @PathVariable(name = "did") Long shopId,
            @PathVariable(name = "id") Long productId,
            @RequestParam(name = "beginTime", required = false)@DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime beginTime,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) ZonedDateTime endTime,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        LocalDateTime localBeginTime=null;
        LocalDateTime localEndTime=null;
        if (beginTime!=null){
            localBeginTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if (endTime!=null){
            localEndTime=beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }

        ReturnObject retVoObject =
                shareService.getProductSuccessfulShares(shopId,productId, localBeginTime,localEndTime,page,pageSize);
        return Common.decorateReturnObject(retVoObject);
    }

    /**
     *根据onsaleId和customerId找最早的未清算的successfulshare
     * @param onsaleId
     * @param customerId
     * @return
     */
    @GetMapping("internal/share")
    public Object getEarliestSuccessfulShares(
            @RequestParam(name = "onsaleId") Long onsaleId,
            @RequestParam(name = "customerId") Long customerId) {
        return shareService.getEarliestSuccessfulShare(onsaleId,customerId);
    }

    /**
     * 返点，设置分享成功记录状态
     * @param id
     * @return
     */
    @PutMapping("internal/beshared/{id}/liquidated")
    public Object setStateliquidated(
            @PathVariable(name = "id") Long id) {
        return shareService.setStateliquidated(id);
    }
}
