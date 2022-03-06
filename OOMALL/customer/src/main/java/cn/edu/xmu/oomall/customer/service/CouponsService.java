package cn.edu.xmu.oomall.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.dao.AddressDao;
import cn.edu.xmu.oomall.customer.dao.CouponDao;
import cn.edu.xmu.oomall.customer.dao.CustomerDao;
import cn.edu.xmu.oomall.customer.dao.ShoppingCartDao;
import cn.edu.xmu.oomall.customer.microservice.CouponService;
import cn.edu.xmu.oomall.customer.microservice.FreightService;
import cn.edu.xmu.oomall.customer.microservice.GoodsService;
import cn.edu.xmu.oomall.customer.microservice.Vo.CouponActivityDetailRetVo;
import cn.edu.xmu.oomall.customer.microservice.Vo.CouponActivityVo;
import cn.edu.xmu.oomall.customer.microservice.Vo.ProductVo;
import cn.edu.xmu.oomall.customer.microservice.Vo.SimpleProductVo;
import cn.edu.xmu.oomall.customer.model.bo.Coupon;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.AddressPo;
import cn.edu.xmu.oomall.customer.model.po.CustomerPo;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RandomCaptcha;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.swagger.models.auth.In;
import net.bytebuddy.asm.Advice;
import org.apache.ibatis.ognl.Token;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Service
public class CouponsService {
    @Autowired
    CouponDao couponDao;

    @Resource
    CouponService couponService;

    @Autowired
    private RedisUtil redisUtil;

    public final static String COUPON_ACTIVITY_KEY = "cou_act_%d";

    /**
     * 买家查看优惠券列表
     */
    public ReturnObject showCoupons(Long userId, Integer page, Integer pageSize){
        if(userId==null){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject(couponDao.selectCouponsByUserId(userId,page,pageSize));
    }

    /**
     * 买家领取活动优惠券
     * 上线状态才能领取
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject receiveCoupon(Long customerId, String customerName, Long couponActivityId) {
        // 获得 couponActivity
        ReturnObject returnObjectCoupon = couponService.getCouponActivityById(couponActivityId);
        if (returnObjectCoupon.getData() == null) {
            return returnObjectCoupon;
        }
        CouponActivityDetailRetVo couponActivityDetailRetVo = (CouponActivityDetailRetVo) returnObjectCoupon.getData();
        if (couponActivityDetailRetVo.getQuantity() == -1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "该优惠活动不需要优惠卷");
        }
        if (LocalDateTime.now().isBefore(couponActivityDetailRetVo.getBeginTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime())) {
            return new ReturnObject(ReturnNo.COUPON_NOTBEGIN);
        }
        if (LocalDateTime.now().isAfter(couponActivityDetailRetVo.getEndTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime())) {
            return new ReturnObject(ReturnNo.COUPON_FINISH);
        }
        ReturnObject couponRetObj = couponDao.selectCouponByCustomerIdAndActivityId(customerId, couponActivityId);
        if (couponRetObj.getData() == null) {
            return couponRetObj;
        }
        List list = (List) couponRetObj.getData();
        if (!list.isEmpty()) {
            return new ReturnObject(ReturnNo.COUPON_EXIST);
        }
        if(!redisUtil.hasKey(String.format(COUPON_ACTIVITY_KEY, couponActivityId))) {
            loadCouponQuantity(couponActivityId, couponActivityDetailRetVo.getQuantity());
        }
        if (couponActivityDetailRetVo.getQuantityType() == (byte) 0) {
            return receiveCouponLimitEach(couponActivityDetailRetVo, customerId, customerName);
        } else {
            return receiveCouponLimitTotal(couponActivityDetailRetVo, customerId, customerName);
        }
    }

    /**
     * 领取优惠券
     * 限定每人数量优惠活动
     */
    private ReturnObject receiveCouponLimitEach(CouponActivityDetailRetVo vo, Long customerId, String customerName) {
        int quantity = vo.getQuantity();
        List<CouponRetVo> retVos = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            Coupon bo = createCoupon(vo, customerId, customerName);
            ReturnObject retObj = couponDao.insertCoupon(bo);
            if (retObj.getData() == null) {
                return retObj;
            }
            Coupon returnBo = (Coupon) retObj.getData();
            CouponRetVo retVo = cloneVo(returnBo, CouponRetVo.class);
            CouponActivityVo couponActivityRetVo = cloneVo(vo, CouponActivityVo.class);
            retVo.setActivity(couponActivityRetVo);
            retVos.add(retVo);
        }
        return new ReturnObject(retVos);
    }

    /**
     * 领取优惠券
     * 限定总数优惠活动
     */
    private ReturnObject receiveCouponLimitTotal(CouponActivityDetailRetVo vo, Long customerId, String customerName) {
        ReturnObject decreaseRetObj = decreaseCouponQuantity(vo.getId());
        if (decreaseRetObj.getCode() != ReturnNo.OK) {
            return decreaseRetObj;
        }
        Coupon bo = createCoupon(vo, customerId, customerName);
        ReturnObject retObj = couponDao.insertCoupon(bo);
        if (retObj.getData() == null) {
            return retObj;
        }
        Coupon returnBo = (Coupon) retObj.getData();
        CouponRetVo retVo = cloneVo(returnBo, CouponRetVo.class);
        CouponActivityVo couponActivityRetVo = cloneVo(vo, CouponActivityVo.class);
        retVo.setActivity(couponActivityRetVo);
        return new ReturnObject(retVo);
    }

    /**
     * 生成优惠券随机编号
     */
    private String randSn(Long customerId, Long activityId) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%010d-%010d-", customerId, activityId));
        builder.append(RandomCaptcha.getRandomString(10));
        return builder.toString();
    }

    /**
     * 生成优惠券
     */
    private Coupon createCoupon(CouponActivityDetailRetVo vo, Long customerId, String customerName) {
        Coupon bo = new Coupon();
        bo.setCouponSn(randSn(customerId, vo.getId()));
        bo.setName(vo.getName() + "优惠券");
        bo.setCustomerId(customerId);
        bo.setActivityId(vo.getId());
        bo.setBeginTime(vo.getBeginTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        bo.setEndTime(vo.getEndTime().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        bo.setState(Coupon.State.RECEIVED.getCode());
        bo.setCreatorId(customerId);
        bo.setCreatorName(customerName);
        return bo;
    }

    /**
     * 加载redis中的优惠券库存
     */
    private ReturnObject loadCouponQuantity(Long couponActivityId, Integer quantity) {
        String key = String.format(COUPON_ACTIVITY_KEY, couponActivityId);
        String scriptPath = "scripts/load.lua";
        DefaultRedisScript script = new DefaultRedisScript();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPath)));
        redisUtil.executeScript(script, Stream.of(key).collect(Collectors.toList()), quantity);
        return new ReturnObject();
    }

    /**
     * 减扣redis中的优惠券库存
     */
    private ReturnObject decreaseCouponQuantity(Long couponActivityId) {
        String key = String.format(COUPON_ACTIVITY_KEY, couponActivityId);
        String scriptPath = "scripts/decrease.lua";
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPath)));
        script.setResultType(Long.class);
        Long result = redisUtil.executeScript(script, Stream.of(key).collect(Collectors.toList()), 1);
        if (result == 0) {
            return new ReturnObject();
        } else {
            return new ReturnObject(ReturnNo.COUPON_FINISH);
        }
    }
}
