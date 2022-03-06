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
public class CustomerControllerTest {
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
     *获得买家的所有状态
     */
    @Test
    @Transactional
    public void GET_testgetUserState() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/customer/states")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     *注册用户
     */
    /**
     *电话号码重复
     * @throws Exception
     */
    @Test
    @Transactional
    public void  POST_testRegisterUser1() throws Exception{
        String json="{\"userName\": \"mybabyw2\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13959235540\", \"email\": \"t223e21st2jcs@test.com\"}";
        String responseString=this.mockMvc.perform(post("/customers")
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":611,\"errmsg\":\"电话已被注册\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     *用户名重复
     * @throws Exception
     */
    @Test
    @Transactional
    public void  POST_testRegisterUser2() throws Exception{
        String json="{\"userName\": \"105048\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13159235540\", \"email\": \"t223e21st2jcs@test.com\"}";
        String responseString=this.mockMvc.perform(post("/customers")
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":613,\"errmsg\":\"用户名已被注册\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     *Email重复
     * @throws Exception
     */
    @Test
    @Transactional
    public void  POST_testRegisterUser3() throws Exception{
        String json="{\"userName\": \"a105048\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13159235540\", \"email\": \"test@email.com\"}";
        String responseString=this.mockMvc.perform(post("/customers")
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":612,\"errmsg\":\"邮箱已被注册\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 空用户名
     * @throws Exception
     */
    @Test
    @Transactional
    public void  POST_testRegisterUser4() throws Exception{
        String json="{\n    \"userName\": null,\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",    \"mobile\": \"6411686886\",  \"email\": \"test@test.com\"}";
        String responseString=this.mockMvc.perform(post("/customers")
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
    正常注册
     */
    @Test
    @Transactional
    public void  POST_testRegisterUser5() throws Exception{
        String json="{ \"userName\": \"customer3\", \"password\": \"Aa123456!@#\",  \"name\": \"测试人\",    \"mobile\": \"13822888388\",  \"email\": \"test@test1.com\"}";
        String responseString=this.mockMvc.perform(post("/customers")
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }


    /**
     * 买家查看自己的信息
     */
    /**
     * 成功查询
     */
    @Test
    @Transactional
    public void GET_testGetSelfInfo1() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/self").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    /**
     * 用户Id不存在
     * @throws Exception
     */
    @Test
    @Transactional
    public void GET_testGetSelfInfo2() throws Exception{
        adminToken=jwtHelper.createToken(99921L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/self").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":504,\"errmsg\":\"操作资源的id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    /**
     * 买家修改自己的信息
     */
    public void PUT_testCustomer01() throws Exception{
        Customerself customerself=new Customerself();
        customerself.setId(1L);
        customerself.setMobile("1312412412");
        customerself.setEmail("124124124");
        customerself.setName("skfjasdgf");
        customerself.setPoint(123L);
        customerself.setUserName("699275");
        String json=JacksonUtil.toJson(customerself);
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/self").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    /**
     * 用户修改密码
     */
    @Test
    @Transactional
    public void PUT_testchangePassword() throws Exception{
        CustomerselfPassword customerselfPassword=new CustomerselfPassword();
        customerselfPassword.setNewPassword("12425125");
        customerselfPassword.setCaptcha("124124");
        customerselfPassword.setUserName("123");
        String json=JacksonUtil.toJson(customerselfPassword);
        adminToken=jwtHelper.createToken(12L,"123",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/password").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":609,\"errmsg\":\"用户名不存在或密码错误\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 用户重置密码
     */
    @Test
    @Transactional
    public void PUT_testReSetPassword() throws Exception{
        ReSetPasswordVo reSetPasswordVo=new ReSetPasswordVo();
        reSetPasswordVo.setEmail("124124124");
        reSetPasswordVo.setName("124124124");
        String json=JacksonUtil.toJson(reSetPasswordVo);
        adminToken=jwtHelper.createToken(12L,"123",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/password/reset").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":608,\"errmsg\":\"登录用户id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 管理员查看所有用户信息
     */
    @Test
    @Transactional
    public void GET_testCustomer02() throws Exception{
        adminToken=jwtHelper.createToken(12L,"123",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/0/customers/all").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").param("page","1").param("pageSize","10"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 用户名密码登入
     */
    @Test
    @Transactional
    public void POST_testlogin1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("customer1");
        loginVo.setPassword("000000");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":609,\"errmsg\":\"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    public void POST_testlogin2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("NotExist");
        loginVo.setPassword("123456");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":609,\"errmsg\":\"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    public void POST_testlogin3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setPassword("123456");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    public void POST_testlogin4() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("537300010");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    public void POST_testlogin5() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("customer1");
        loginVo.setPassword("123456");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    @Test
    @Transactional
    public void POST_testlogin6() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("575332");
        loginVo.setPassword("123456");
        String json = JacksonUtil.toJson(loginVo);
        adminToken=jwtHelper.createToken(16666L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(post("/login").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":610,\"errmsg\":\"用户被禁止登录\"}";
        JSONAssert.assertEquals(expected,responseString,false);
        System.out.println(responseString);
    }

    /**
     * 用户登出
     */
    @Test
    @Transactional
    public void GET_testlogout() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/logout").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 管理员查看任意买家信息
     */
    @Test
    @Transactional
    public void GET_testgetUserById() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shop/0/customers/50131").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 管理员查看任意买家信息
     */
    @Test
    @Transactional
    public void GET_testgetUserByInfo() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(get("/shops/0/customers").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 管理员封禁买家
     */
    @Test
    @Transactional
    public void PUT_testbanUser() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/shops/0/customers/3/ban").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

    /**
     * 管理员解禁买家
     */
    @Test
    @Transactional
    public void PUT_testreleaseUser() throws Exception{
        adminToken=jwtHelper.createToken(1L,"699275",0L, 3600,0);
        String responseString=this.mockMvc.perform(put("/shops/0/customers/3/release").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected,responseString,false);
    }

}
