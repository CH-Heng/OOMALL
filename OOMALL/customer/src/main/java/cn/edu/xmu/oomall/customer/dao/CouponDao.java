package cn.edu.xmu.oomall.customer.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.mapper.AddressPoMapper;
import cn.edu.xmu.oomall.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.oomall.customer.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.oomall.customer.mapper.CouponPoMapper;
import cn.edu.xmu.oomall.customer.microservice.Vo.SimpleProductVo;
import cn.edu.xmu.oomall.customer.model.bo.Address;
import cn.edu.xmu.oomall.customer.model.bo.Coupon;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.*;
import cn.edu.xmu.oomall.customer.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.bytebuddy.asm.Advice;
import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.util.*;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Repository
public class CouponDao {
    @Autowired
    CouponPoMapper couponPoMapper;

    /**
     * 根据用户id查看优惠券
     */
    public ReturnObject selectCouponsByUserId(Long userId,Integer page,Integer pageSize){
        CouponPoExample example=new CouponPoExample();
        CouponPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        try{
            PageHelper.startPage(page,pageSize);
            List<CouponPo> poList=couponPoMapper.selectByExample(example);
            PageInfo<CouponPo> pageInfo=new PageInfo<>(poList);
            return Common.getPageRetVo(new ReturnObject(pageInfo),SimpleCouponVo.class);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据customerId和activityId获得优惠券
     */
    public ReturnObject selectCouponByCustomerIdAndActivityId(Long customerId, Long activityId) {
        CouponPoExample example = new CouponPoExample();
        CouponPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andActivityIdEqualTo(activityId);
        try {
            List<CouponPo> pos = couponPoMapper.selectByExample(example);
            return Common.getListRetVo(new ReturnObject(pos), Coupon.class);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     *新增优惠券
     */
    public ReturnObject insertCoupon(Coupon coupon){
        try{
            CouponPo couponPo=cloneVo(coupon,CouponPo.class);
            couponPoMapper.insert(couponPo);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
