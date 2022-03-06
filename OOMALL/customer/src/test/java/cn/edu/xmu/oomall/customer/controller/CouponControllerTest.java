package cn.edu.xmu.oomall.customer.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.CustomerApplication;
import cn.edu.xmu.oomall.customer.microservice.CouponService;
import cn.edu.xmu.oomall.customer.microservice.GoodsService;
import cn.edu.xmu.oomall.customer.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.auth0.jwt.JWTCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.xmu.oomall.customer.model.vo.ProductFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = CustomerApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
public class CouponControllerTest {
    private static String adminToken;
    private static JwtHelper jwtHelper = new JwtHelper();
    @Autowired
    MockMvc mvc;
    @MockBean
    private RedisUtil redisUtil;

    private String token;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ProductFactory productFactory;

    @Autowired
    private CouponActivityFactory couponActivityFactory;


    @BeforeEach
    public void init()
    {
        JwtHelper jwtHelper = new JwtHelper();
        token  = jwtHelper.createToken(13L,"348671",0L,1,3600);

    }
//    /**
//     * 获取优惠券状态
//     */
//    @Test
//    @Transactional
//    public void GET_testgetCouponState() throws Exception{
//        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
//        String responseString=this.mockMvc.perform(get("/coupons/states")
//                        .contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expected,responseString,false);
//    }

    /**
     * 领取优惠券
     */
    @Test
    @Transactional
    public void POST_testgetCoupon() throws Exception{
        String ret1 = mvc.perform(post("/couponactivities/1/coupons")).andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,ret1,false);
    }

    @Test
    void getAllState() throws Exception {
        String ret1 = mvc.perform(get("/coupons/states")).andReturn().getResponse().getContentAsString();
        //System.err.println(ret1);
        JSONAssert.assertEquals("{\"errno\":0,\"data\":[{\"code\":1,\"name\":\"已领取\"},{\"code\":3,\"name\":\"已失效\"},{\"code\":2,\"name\":\"已使用\"}],\"errmsg\":\"成功\"}",
                ret1,false);
    }
}
