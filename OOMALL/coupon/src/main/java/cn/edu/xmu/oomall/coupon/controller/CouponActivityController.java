package cn.edu.xmu.oomall.coupon.controller;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.model.bo.CouponActivity;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.model.vo.DiscountItemVo;
import cn.edu.xmu.oomall.coupon.service.CouponActivityService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author RenJieZheng 22920192204334
 */
/**
 * @author qingguo Hu 22920192204208
 */
@RestController
@RefreshScope
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
public class CouponActivityController {
    @Autowired
    CouponActivityService couponActivityService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    static final Integer IMAGE_MAX_SIZE=1000000;
    /**
     * 查看优惠活动模块的所有活动
     * @return List<Map<String, Object>>
     */
    @GetMapping("couponactivities/states")
    public Object showAllState(){
        return  Common.decorateReturnObject(couponActivityService.showAllState());
    }

    /**
     * 管理员新建己方优惠活动
     * @param shopId 店铺id
     * @param couponActivityVo 优惠券信息
     * @return 插入结果
     */
    @Audit(departName = "shops")
    @PostMapping("shops/{shopId}/couponactivities")
    public Object addCouponActivity(@PathVariable Long shopId,
                                    @LoginUser Long userId, @LoginName String userName,
                                    @Valid @RequestBody CouponActivityVo couponActivityVo,
                                    HttpServletResponse httpServletResponse, BindingResult bindingResult
    ){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        //对输入数据进行合法性判断
        // 如果开始时间晚于结束时间
        if(couponActivityVo.getBeginTime()!=null&&couponActivityVo.getEndTime()!=null){
            if(couponActivityVo.getBeginTime().compareTo(couponActivityVo.getEndTime()) > 0){
                return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME));
            }
        }
        // 优惠卷领卷时间晚于活动开始时间
        if(couponActivityVo.getCouponTime()!=null&&couponActivityVo.getBeginTime()!=null){
            if(couponActivityVo.getCouponTime().compareTo(couponActivityVo.getBeginTime()) > 0){
                return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.COUPON_LATE_COUPONTIME));
            }
        }
        ReturnObject returnObject1 = couponActivityService.addCouponActivity(userId,userName,shopId,couponActivityVo);
        if(returnObject1.getCode()==ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }
        return Common.decorateReturnObject(returnObject1);
    }
    /**
     * 查看店铺所有状态的优惠活动列表
     * @param shopId 店铺id
     * @param state 状态
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表
     */
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/couponactivities")
    public Object showOwnInvalidCouponActivities(@PathVariable Long shopId,
                                                 @LoginUser Long userId, @LoginName String userName,
                                                 @RequestParam(required = false) Byte state,
                                                 @RequestParam(required = false,defaultValue = "1") Integer page,
                                                 @RequestParam(required = false,defaultValue = "10") Integer pageSize
    ){
        return Common.decorateReturnObject(couponActivityService.showOwnInvalidCouponActivities(userId,userName,shopId,state,page,pageSize));
    }

    /**
     * 上传图片文件
     * @param shopId 店铺id
     * @param id 活动id
     * @param request 请求
     * @return 上传结果
     */
    @Audit(departName = "shops")
    @PostMapping("shops/{shopId}/couponactivities/{id}/uploadImg")
    public Object addCouponActivityImageUrl(@PathVariable Long shopId,
                                            @PathVariable Long id,
                                            @LoginUser Long userId, @LoginName String userName,
                                            HttpServletRequest request) {

        //对输入数据进行合法性判断
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        if(files.size()<=0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.FIELD_NOTVALID));
        }
        MultipartFile multipartFile=files.get(0);
        //图片超限
        if(multipartFile.getSize()>IMAGE_MAX_SIZE){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.IMG_SIZE_EXCEED));
        }
        return Common.decorateReturnObject(couponActivityService.addCouponActivityImageUrl(userId,userName,id,shopId,multipartFile));

    }

    /**
     * 查看所有的上线优惠活动列表
     * @param shopId 店铺id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表 List<CouponActivityRetVo>
     */
    @GetMapping("couponactivities")
    public Object showOwnCouponActivities(@RequestParam(required = false) Long shopId,
                                          @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginTime,
                                          @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endTime,
                                          @RequestParam(required = false,defaultValue = "1") Integer page,
                                          @RequestParam(required = false,defaultValue = "10") Integer pageSize
    ){
        //对输入数据进行合法性判断
        // 如果开始时间晚于结束时间
        if(beginTime != null && beginTime.compareTo(endTime) > 0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME));
        }
        return Common.decorateReturnObject(couponActivityService.showOwnCouponActivities(shopId,beginTime,endTime,page,pageSize));

    }

    /**
     * 查看店铺的所有状态优惠活动列表
     * @param shopId 店铺id
     * @param state 状态
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页
     * @param pageSize 页大小
     * @return 优惠活动列表 List<CouponActivityRetVo>
     */
    @GetMapping("shop/{shopId}/couponactivities")
    public Object showOwnCouponaAtivities1(@PathVariable Long shopId,
                                           @RequestParam(required = false) Byte state,
                                           @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime beginTime,
                                           @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") @RequestParam(required = false) ZonedDateTime endTime,
                                           @RequestParam(required = false,defaultValue = "1") Integer page,
                                           @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        //对输入数据进行合法性判断
        // 如果开始时间晚于结束时间
        if(beginTime.compareTo(endTime) > 0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME));
        }
        return Common.decorateReturnObject(couponActivityService.showOwnCouponActivities1(shopId,beginTime,endTime,state,page,pageSize));
    }

    /**
     * 查看优惠活动详情
     * @param id 活动id
     * @param shopId 店铺id
     * @return 优惠活动信息
     */
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/couponactivities/{id}")
    public Object showOwnCouponActivityInfo(@PathVariable Long shopId,
                                            @PathVariable Long id,
                                            @LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(couponActivityService.showOwnCouponActivityInfo(userId,userName,id,shopId));
    }


    /**
     * @author qingguo Hu 22920192204208
     */

    @ApiOperation(value = "查看优惠活动中的商品")
    @GetMapping("couponactivities/{id}/products")
    public Object listProductsByCouponActivityId(@ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                                 @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                 @ApiParam(value = "每页数目") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        ReturnObject<PageInfo<Object>> retPageInfo =
                couponActivityService.listProductsByCouponActivityId(couponActivityId, pageNumber, pageSize);
        return Common.decorateReturnObject(retPageInfo);

    }


    @ApiOperation(value = "查看商品的上线的优惠活动")
    @GetMapping("products/{id}/couponactivities")
    public Object listCouponActivitiesByProductId(@ApiParam(value = "货品ID", required = true) @PathVariable("id") Long productId,
                                                  @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                  @ApiParam(value = "每页数目") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject<PageInfo<Object>> retPageInfo =
                couponActivityService.listCouponActivitiesByProductId(productId, pageNumber, pageSize);

        return Common.decorateReturnObject(retPageInfo);
    }

    @ApiOperation(value = "管理员查看优惠活动中的商品")
    @GetMapping("/shops/{shopId}/couponactivities/{id}/onsales")
    public Object listOnsalesByCouponActivityId(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                                @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                                @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                @ApiParam(value = "每页数目") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize ) {

        ReturnObject returnObject = couponActivityService.listOnsalesByCouponActivityId(shopId, couponActivityId, pageNumber, pageSize);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "管理员修改己方某优惠活动")
    @PutMapping("shops/{shopId}/couponactivities/{id}")
    @Audit(departName = "shops")
    public Object updateCouponActivity(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                       @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                       @ApiParam(value = "可修改的优惠活动信息", required = true) @Validated @RequestBody CouponActivityVo couponActivityVo,
                                       @LoginUser Long userId, @LoginName String userName, BindingResult bindingResult) {

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }

        if (couponActivityVo.getBeginTime() != null && couponActivityVo.getEndTime() != null) {
            if (couponActivityVo.getBeginTime().isAfter(couponActivityVo.getEndTime())) {
                return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME));
            }
        }

        ReturnObject returnObject = couponActivityService.updateCouponActivity(userId, userName, shopId, couponActivityId, couponActivityVo, null);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "管理员物理删除己方某优惠活动")
    @DeleteMapping("shops/{shopId}/couponactivities/{id}")
    @Audit(departName = "shops")
    public Object deleteCouponActivity(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                       @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                       @LoginUser Long userId, @LoginName String userName) {

        ReturnObject returnObject = couponActivityService.deleteCouponActivity(userId, userName, shopId, couponActivityId);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "管理员为己方某优惠券活动新增限定范围")
    @PostMapping("shops/{shopId}/couponactivities/{id}/onsales/{sid}")
    @Audit(departName = "shops")
    public Object insertCouponOnsale(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                     @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                     @ApiParam(value = "销售活动ID", required = true) @PathVariable("sid") Long onsaleId,
                                     @LoginUser Long userId, @LoginName String userName) {

        ReturnObject returnObject = couponActivityService.insertCouponOnsale(userId, userName, shopId, couponActivityId, onsaleId);
        if(returnObject.getCode()==ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "店家删除己方某优惠券活动的某限定范围")
    @DeleteMapping("shops/{shopId}/coupononsale/{id}")
    @Audit(departName = "shops")
    public Object deleteCouponOnsale(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                     @ApiParam(value = "couponOnsaleId", required = true) @PathVariable("id") Long couponOnsaleId,
                                     @LoginUser Long userId, @LoginName String userName) {

        ReturnObject returnObject = couponActivityService.deleteCouponOnsale(userId, userName, shopId, couponOnsaleId);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "上线优惠活动")
    @PutMapping("shops/{shopId}/couponactivities/{id}/online")
    @Audit(departName = "shops")
    public Object updateCouponActivityToOnline(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                               @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                               @LoginUser Long userId, @LoginName String userName) {

        ReturnObject returnObject = couponActivityService.updateCouponActivity(userId, userName, shopId, couponActivityId, null, CouponActivity.State.ONLINE);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "下线优惠活动")
    @PutMapping("shops/{shopId}/couponactivities/{id}/offline")
    @Audit(departName = "shops")
    public Object updateCouponActivityToOffline(@ApiParam(value = "商店ID", required = true) @PathVariable("shopId") Long shopId,
                                                @ApiParam(value = "优惠活动ID", required = true) @PathVariable("id") Long couponActivityId,
                                                @LoginUser Long userId, @LoginName String userName) {

        ReturnObject returnObject = couponActivityService.updateCouponActivity(userId, userName, shopId, couponActivityId, null, CouponActivity.State.OFFLINE);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @author Zijun Min
     * 根据设定的优惠活动计算当前有效的商品优惠价格
     */
    @PutMapping("/internal/discountprices")
    @Audit
    public Object calculateDiscount(@LoginUser Long LoginUser, @LoginName String loginName, @RequestBody List<DiscountItemVo>items)
            throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        ReturnObject returnObject=couponActivityService.calculateActivityDiscount(items);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查看优惠活动详情
     * @param id 活动id
     * @return 优惠活动信息
     */
    @GetMapping("/internal/couponactivities/{id}")
    public Object showOwnCouponActivityInfo(@PathVariable Long id){
        return couponActivityService.showCouponActivityInfo(id);
    }
}
