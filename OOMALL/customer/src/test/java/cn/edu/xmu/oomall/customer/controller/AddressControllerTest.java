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
public class AddressControllerTest {
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
     * 买家新增地址
     */
    @Test
    @Transactional
    public void POST_testaddAddress() throws Exception{
        AddAddressVo addAddressVo=new AddAddressVo();
        addAddressVo.setRegionId(1414L);
        addAddressVo.setConsignee("赵全昆");
        addAddressVo.setMobile("13959235540");
        addAddressVo.setDetail("安分守己开发咖喱饭");
        String json=JacksonUtil.toJson(addAddressVo);
        adminToken=jwtHelper.createToken(44L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/addresses").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家查询所有已有的地址信息
     */
    @Test
    @Transactional
    public void GET_testqueryAddress() throws Exception {
        String responseString = this.mockMvc.perform(get("/addresses").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").param("page","1").param("pageSize","10"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }



    /**
     * 买家设置默认地址
     */
    @Test
    @Transactional
    public void PUT_testsetDefaultAddress() throws Exception{
        adminToken=jwtHelper.createToken(44L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/addresses/23/default").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家修改地址
     */
    @Test
    @Transactional
    public void PUT_testchangeAddressInfo() throws Exception{
        AddAddressVo addAddressVo=new AddAddressVo();
        addAddressVo.setDetail("afgakfkaafk");
        addAddressVo.setMobile("12441441412");
        addAddressVo.setConsignee("afadfaf");
        addAddressVo.setRegionId(14214L);
        String json=JacksonUtil.toJson(addAddressVo);
        adminToken=jwtHelper.createToken(2L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/addresses/2").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 买家删除地址
     */
    @Test
    @Transactional
    public void DELETE_testdelAddress() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(delete( "/addresses/1").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }
}
