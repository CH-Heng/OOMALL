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
public class ShoppingCartControllerTest {
    private static String adminToken;
    private static JwtHelper jwtHelper = new JwtHelper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ProductFactory productFactory;

    @Autowired
    private CouponActivityFactory couponActivityFactory;

    /**
     * 买家获得购物车列表
     */
    @Test
    @Transactional
    public void GET_testqueryCarts() throws Exception{
        Mockito.when(goodsService.getProduct(10006L)).thenReturn(new ReturnObject(productFactory.create(10006L)));
        Mockito.when(couponService.getActivityByProduct(10006L)).thenReturn(new ReturnObject(couponActivityFactory.create(10006L)));
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/carts").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").param("page","1").param("pageSize","10"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家添加商品到购物车
     */
    @Test
    @Transactional
    public void POST_testaddToCart() throws Exception{
        Mockito.when(goodsService.getProduct(10006L)).thenReturn(new ReturnObject(productFactory.create(10006L)));
        AddCartVo addCartVo=new AddCartVo();
        addCartVo.setProductId(10006L);
        addCartVo.setQuantity(1L);
        String json=JacksonUtil.toJson(addCartVo);
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/carts").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家清空购物车
     */
    @Test
    @Transactional
    public void DELETE_testclearGoods() throws Exception{
        adminToken=jwtHelper.createToken(5211L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(delete("/carts").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家修改购物车单件商品信息
     */
    @Test
    @Transactional
    public void PUT_testchangeCartInfo() throws Exception{
        ChangeCartInfoVo changeCartInfoVo=new ChangeCartInfoVo();
        changeCartInfoVo.setProductId(2198L);
        changeCartInfoVo.setQuantity(4L);
        String json = JacksonUtil.toJson(changeCartInfoVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/cart/1").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家删除购物车中商品
     */
    @Test
    @Transactional
    public void DELETE_testdelGoods() throws Exception{
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(delete("/cart/1").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }
}
