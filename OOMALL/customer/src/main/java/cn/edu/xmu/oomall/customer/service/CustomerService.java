package cn.edu.xmu.oomall.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.dao.AddressDao;
import cn.edu.xmu.oomall.customer.dao.CustomerDao;
import cn.edu.xmu.oomall.customer.dao.ShoppingCartDao;
import cn.edu.xmu.oomall.customer.microservice.CouponService;
import cn.edu.xmu.oomall.customer.microservice.FreightService;
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
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RandomCaptcha;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.x.protobuf.Mysqlx;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.swagger.models.auth.In;
import net.bytebuddy.asm.Advice;
import org.apache.ibatis.ognl.Token;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Service
public class CustomerService {
    @Autowired
    AddressDao addressDao;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    ShoppingCartDao shoppingCartDao;

    @Resource
    GoodsService goodsService;

    @Resource
    CouponService couponService;

    @Resource
    FreightService freightService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${customer.login.expire}")
    private Integer jwtExpireTime;

    @Value("${customer.captcha.expire}")
    private Long captchaExpireTime;

    public final static String CAPTCHA_KEY = "cap_%s";

    public final static String CUSTOMER_KEY = "cus_%d";


    /**
     * 获得买家的所有状态
     */
    public ReturnObject getUserState(){
        List<UserStateVo> list=new ArrayList<>();
        for(Customer.State value : Customer.State.values()){
            UserStateVo userStateVo=new UserStateVo(value.getCode(), value.getDescription());
            list.add(userStateVo);
        }
        return new ReturnObject<>(list);
    }

    /**
     * 注册用户
     */
    public ReturnObject registerUser(CreateUserVo createUserVo){
        ReturnObject returnObject1;
        ReturnObject returnObject2;
        ReturnObject returnObject3;
        returnObject2=customerDao.searchByUserName(createUserVo.getUserName());
        if(returnObject2.getCode()==ReturnNo.OK){
            return new ReturnObject(ReturnNo.CUSTOMER_NAMEEXIST,"用户名已被注册");
        }
        returnObject3=customerDao.searchByEmail(createUserVo.getEmail());
        if(returnObject3.getCode()==ReturnNo.OK){
            return new ReturnObject(ReturnNo.CUSTOMER_EMAILEXIST,"邮箱已被注册");
        }
        returnObject1=customerDao.searchByMobile(createUserVo.getMobile());
        if(returnObject1.getCode()==ReturnNo.OK){
            return new ReturnObject(ReturnNo.CUSTOMER_MOBILEEXIST,"电话已被注册");
        }
        return new ReturnObject(customerDao.insertUser(createUserVo));
    }

    /**
     *买家查看自己信息
     * @param userId 买家id
     * @return 买家信息
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject<Customerself> showOwnCustomerSelf(Long userId,String userName){
        ReturnObject returnObject=customerDao.showCustomer(userId);
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        Customer customer=(Customer) returnObject.getData();
        if(!customer.getId().equals(userId)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(new Customerself(customer));
    }

    /**
     * 买家修改自己信息
     * @param userId
     * @param userName
     * @param customerself
     * @return
     */
    public ReturnObject changeMyself(Long userId,String userName,Customerself customerself){
        Customer customer=(Customer) cloneVo(customerself,Customer.class);
        customer.setUserName(userName);
        setPoModifiedFields(customer,userId,userName);
        return customerDao.updateCustomer(customer);
    }

    /**
     * 用户修改密码
     */
    public ReturnObject changePassword(CustomerselfPassword customerselfPassword){
        String key=String.format(CAPTCHA_KEY,customerselfPassword.getCaptcha());
        if(!redisUtil.hasKey(key)){
            return new ReturnObject<>(ReturnNo.CUSTOMER_INVALID_ACCOUNT);
        }
        try{
            Long id=(Long) redisUtil.get(key);
            Customer customer=cloneVo(customerselfPassword,Customer.class);
            setPoModifiedFields(customer,id,customerselfPassword.getUserName());
            return new ReturnObject(customerDao.changePassword(customer));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 用户重置密码
     */
    public ReturnObject reSetPassword(ReSetPasswordVo reSetPasswordVo){
        ReturnObject returnObject=customerDao.searchByUserName(reSetPasswordVo.getName());
        if(returnObject.getData()==null) {
            return returnObject;
        }
        Customer customer=(Customer) returnObject.getData();
        String captcha= RandomCaptcha.getRandomString(6);
        while (redisUtil.hasKey(captcha)){
            captcha = RandomCaptcha.getRandomString(6);
        }
        String key=String.format(CAPTCHA_KEY,captcha);
        redisUtil.set(key,customer.getId());
        CaptchaVo captchaVo=new CaptchaVo();
        captchaVo.captcha=captcha;
        return new ReturnObject(captchaVo);
    }

    /**
     * 平台管理员获取所有用户列表
     */
    public ReturnObject getAllUsers(Long userId,String userName,Integer id,Integer page,Integer pageSize){
        if(id!=null){
            if(id!=0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        }
        return customerDao.getAllUsers(page,pageSize);
    }

    /**
     * 用户名密码登录
     */
    public ReturnObject login(LoginVo loginVo, HttpServletResponse httpServletResponse){
        if((loginVo.getPassword()==null)||(loginVo.getPassword().length()==0)||(loginVo.getUserName()==null)||(loginVo.getUserName().length()==0)){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }
        ReturnObject returnObject=customerDao.searchByUserName(loginVo.getUserName());
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        Customer customer=(Customer)returnObject.getData();
        if(!customer.getPassword().equals(loginVo.getPassword())){
            return new ReturnObject(ReturnNo.CUSTOMER_INVALID_ACCOUNT);
        }
        if((int)customer.getState()==Customer.State.BANNED.getCode()){
            return new ReturnObject(ReturnNo.CUSTOMER_FORBIDDEN);
        }
        String key=String.format(CUSTOMER_KEY,returnObject.getData());
        String token = new JwtHelper().createToken((Long)returnObject.getData(), loginVo.getUserName(), -1L, -1, jwtExpireTime);
        redisUtil.set(key,token,jwtExpireTime);
        return new ReturnObject(token);
    }

    /**
     * 用户登出
     */
    public ReturnObject logout(Long userId,String userName){
        try{
            String key = String.format(CUSTOMER_KEY, userId);
            redisUtil.del(key);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 平台管理员查看任意买家信息
     */
    public ReturnObject<Customerself> getUserById(Long shopId,Long id)
    {
        if(shopId!=0){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject=customerDao.showCustomer(id);
        if(returnObject.getCode()!= ReturnNo.OK){
            return returnObject;
        }
        Customer customer=(Customer) returnObject.getData();
        if(!customer.getId().equals(id)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(new Customerself(customer));
    }

    /**
     * 管理员封禁买家
     */
    public ReturnObject banUser(Long shopId,Long id,Long userId,String userName) {
        if (shopId != 0){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject=customerDao.showCustomer(id);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        return new ReturnObject(customerDao.banUser(id,userId,userName));
    }

    /**
     * 管理员解禁买家
     */
    public ReturnObject releaseUser(Long shopId,Long id,Long userId,String userName) {
        if (shopId != 0){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject=customerDao.showCustomer(id);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        return new ReturnObject(customerDao.releaseUser(id,userId,userName));
    }
}
