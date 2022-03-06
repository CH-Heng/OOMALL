package cn.edu.xmu.oomall.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.dao.AddressDao;
import cn.edu.xmu.oomall.customer.dao.CustomerDao;
import cn.edu.xmu.oomall.customer.dao.ShoppingCartDao;
import cn.edu.xmu.oomall.customer.microservice.CouponService;
import cn.edu.xmu.oomall.customer.microservice.GoodsService;
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
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Service
public class ShoppingCartService {
    @Autowired
    ShoppingCartDao shoppingCartDao;

    @Resource
    GoodsService goodsService;

    @Resource
    CouponService couponService;

    /**
     * 买家获取购物车列表
     */
    public ReturnObject getCart(Long userId,Integer page,Integer pageSize){
        ReturnObject returnObjectSelect=shoppingCartDao.showAllCarts(userId,page,pageSize);
        PageInfo pageInfo=(PageInfo) returnObjectSelect.getData();
        List<ShoppingCart> list=pageInfo.getList();
        List<ShoppingCartRetVo> retVoList=new ArrayList<>();
        for (ShoppingCart shoppingCart: list) {
            ShoppingCartRetVo shoppingCartRetVo=cloneVo(shoppingCart,ShoppingCartRetVo.class);
            ReturnObject returnObjectProduct=goodsService.getProduct(shoppingCart.getProductId());
            if(returnObjectProduct.getData()==null){
                return returnObjectProduct;
            }
            SimpleProductVo simpleProductVo=(SimpleProductVo) returnObjectProduct.getData();
            shoppingCartRetVo.setProduct(simpleProductVo);
            ReturnObject returnObjectCoupon=couponService.getActivityByProduct(shoppingCart.getProductId());
            if(returnObjectCoupon.getData()==null){
                return returnObjectCoupon;
            }
            List<CouponActivityVo> couponActivityVos=(List<CouponActivityVo>) returnObjectCoupon.getData();
            shoppingCartRetVo.setCouponActivity(couponActivityVos);
            retVoList.add(shoppingCartRetVo);
        }
        pageInfo.setList(retVoList);
        return Common.getPageRetVo(new ReturnObject(pageInfo),ShoppingCartRetVo.class);
    }

    /**
     * 买家将商品加入购物车
     */
    public ReturnObject addToCart(Long userId,String userName,AddCartVo addCartVo){
        ReturnObject returnObject=goodsService.getProduct(addCartVo.getProductId());
        ProductVo productVo=null;
        productVo=(ProductVo) returnObject.getData();
        if(productVo==null){
            return returnObject;
        }
        return shoppingCartDao.addToCart(userId,userName,productVo.getPrice(),addCartVo);
    }

    /**
     * 买家清空购物车
     */
    public ReturnObject clearGoods(Long userId){
        return shoppingCartDao.clearGoods(userId);
    }

    /**
     * 买家修改购物车单个商品的数量或规格
     */
    public ReturnObject changeCartInfo(Long userId,Long id,ChangeCartInfoVo changeCartInfoVo){
        ReturnObject returnObject=shoppingCartDao.searchByCartId(userId,id,changeCartInfoVo.getProductId());
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        return new ReturnObject(shoppingCartDao.changeCartInfo(id,changeCartInfoVo));
    }

    /**
     * 买家删除购物车中商品
     */
    public ReturnObject delGoods(Long userId,Long id){
        ReturnObject returnObject=shoppingCartDao.delGoods(id,userId);
        return returnObject;
    }
}
