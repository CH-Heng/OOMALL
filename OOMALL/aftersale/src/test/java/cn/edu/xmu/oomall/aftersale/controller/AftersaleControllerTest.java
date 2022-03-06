package cn.edu.xmu.oomall.aftersale.controller;

import cn.edu.xmu.oomall.aftersale.microservice.CustomerService;
import cn.edu.xmu.oomall.aftersale.microservice.OrderService;
import cn.edu.xmu.oomall.aftersale.microservice.PaymentService;
import cn.edu.xmu.oomall.aftersale.microservice.FreightService;
import cn.edu.xmu.oomall.aftersale.microservice.vo.*;
import cn.edu.xmu.oomall.aftersale.model.vo.*;
import cn.edu.xmu.oomall.aftersale.service.mq.AftersalePaymentListener;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AftersaleControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean(name = "cn.edu.xmu.oomall.aftersale.microservice.CustomerService")
    private CustomerService customerService;

    @MockBean(name = "cn.edu.xmu.oomall.aftersale.microservice.FreightService")
    private FreightService freightService;

    @MockBean(name = "cn.edu.xmu.oomall.aftersale.microservice.PaymentService")
    private PaymentService paymentService;

    @MockBean(name = "cn.edu.xmu.oomall.aftersale.microservice.OrderService")
    private OrderService orderService;

    @Autowired
    private AftersalePaymentListener aftersalePaymentListener;

    private static final Logger logger = LoggerFactory.getLogger(AftersaleControllerTest.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    private static final Locale LOCALE=Locale.CHINA;
    private DateTimeFormatter df=DateTimeFormatter.ofPattern(DATE_TIME_FORMAT,LOCALE);
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
    private static String adminToken1 = jwtHelper.createToken(2L, "shopadmin", 1L, 1, 3600);
    private static String customerToken=jwtHelper.createToken(5L,"wxt",1L,1,3600);
    private static String customerToken1=jwtHelper.createToken(5L,"customer",0L,2,36000);
    private static String customerTokenWithIdSix=jwtHelper.createToken(6L,"customer",0L,2,36000);
    /**
     * 获取售后所有状态
     * @author wxt
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAftersaleStatesTest() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"新建态\"},{\"code\":1,\"name\":\"待买家发货\"},{\"code\":2,\"name\":\"买家已发货\"},{\"code\":3,\"name\":\"待退款\"},{\"code\":4,\"name\":\"待店家发货\"},{\"code\":5,\"name\":\"店家已发货\"},{\"code\":6,\"name\":\"已结束\"},{\"code\":7,\"name\":\"已取消\"},{\"code\":8,\"name\":\"待支付\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }


    /**
     * 买家查询所有的售后单信息
     * @author wxt
     */
    @Test
    @Transactional
    //不添加任何查询条件
    public void getAllAftersaleByUserTest1() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = " {\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //添加售后状态查询 有返回记录
    public void getAllAftersaleByUserTest2() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?state=1")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = " {\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"serviceSn\":\"1\",\"type\":1,\"reason\":null,\"price\":100,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //添加售后状态查询 返回为空
    public void getAllAftersaleByUserTest3() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?state=5")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = " {\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //添加开始时间和结束时间查询
    public void getAllAftersaleByUserTest4() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?beginTime=2021-11-01T10:10:10.000+08:00&endTime=2023-11-11T16:10:10.000+08:00")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //添加状态和时间查询
    public void getAllAftersaleByUserTest5() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?state=1&beginTime=2021-11-01T10:10:10.000+08:00&endTime=2022-11-11T16:10:10.000+08:00")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"serviceSn\":\"1\",\"type\":1,\"reason\":null,\"price\":100,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //开始时间晚于结束时间
    public void getAllAftersaleByUserTest6() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?beginTime=2021-11-01T10:10:10.000+08:00&endTime=2021-10-11T16:10:10.000+08:00")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //输入的state不合法
    public void getAllAftersaleByUserTest7() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales?state=-1")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"输入的state不合法\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }


    /**
     * 管理员查询所有的售后单信息
     * @author wxt
     */
    @Test
    @Transactional
    //不添加任何查询条件(平台管理员查询某个店铺的售后单)
    public void getAllAftersaleByAdminTest1() throws Exception {
        String responseString = this.mvc.perform(get("/shops/2/aftersales")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"serviceSn\":\"2\",\"type\":2,\"reason\":null,\"price\":100,\"quantity\":2,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":5}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //不添加任何查询条件(平台管理员查询所有店铺的售后单)
    public void getAllAftersaleByAdminTest2() throws Exception {
        String responseString = this.mvc.perform(get("/shops/0/aftersales")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //不添加任何查询条件（店铺管理员查询自己店铺的）
    public void getAllAftersaleByAdminTest3() throws Exception {
        String responseString = this.mvc.perform(get("/shops/1/aftersales")
                .header("authorization", adminToken1).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"serviceSn\":\"1\",\"type\":1,\"reason\":null,\"price\":100,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //不添加任何查询条件（店铺管理员查询不是自己店铺的售后单返回错误信息）
    public void getAllAftersaleByAdminTest4() throws Exception {
        String responseString = this.mvc.perform(get("/shops/2/aftersales")
                .header("authorization", adminToken1).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //添加售后单状态查询，合法
    public void getAllAftersaleByAdminTest5() throws Exception {
        String responseString = this.mvc.perform(get("/shops/2/aftersales?state=5")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"serviceSn\":\"2\",\"type\":2,\"reason\":null,\"price\":100,\"quantity\":2,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":5}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //添加售后单状态查询，状态不合法
    public void getAllAftersaleByAdminTest6() throws Exception {
        String responseString = this.mvc.perform(get("/shops/2/aftersales?state=-1")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"输入的state不合法\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //添加售后单状态和类型查询，合法
    public void getAllAftersaleByAdminTest7() throws Exception {
        String responseString = this.mvc.perform(get("/shops/0/aftersales?type=2&state=5")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"serviceSn\":\"2\",\"type\":2,\"reason\":null,\"price\":100,\"quantity\":2,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":5}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //添加售后单状态、类型、开始时间和结束时间查询，都合法
    public void getAllAftersaleByAdminTest8() throws Exception {
        String responseString = this.mvc.perform(get("/shops/0/aftersales?type=1&state=5&beginTime=2021-11-01T10:10:10.000+08:00&endTime=2022-10-11T16:10:10.000+08:00")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //开始时间晚于结束时间
    public void getAllAftersaleByAdminTest9() throws Exception {
        String responseString = this.mvc.perform(get("/shops/0/aftersales?type=0&state=5&beginTime=2021-11-01T10:10:10.000+08:00&endTime=2021-10-11T16:10:10.000+08:00")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }


    /**
     * 买家根据售后单id查询售后单信息
     */
    @Test
    @Transactional
    //成功查询自己的售后单
    public void getAllAftersaleByUserIdTest1() throws Exception {
        Mockito.when(customerService.getSimpleCustomer(5L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleVo(5L,"123")));
        Mockito.when(freightService.getRegionById(1L)).thenReturn(new InternalReturnObject<>(new SimpleRegionVo(1L,"中国")));
        String responseString = this.mvc.perform(get("/aftersales/1")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //输入的售后单id不存在
    public void getAllAftersaleByUserIdTest2() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales/-1")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //输入的售后单id不是自己的售后单
    public void getAllAftersaleByUserIdTest3() throws Exception {
        String responseString = this.mvc.perform(get("/aftersales/2")
                .header("authorization", customerToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":505,\"errmsg\":\"该顾客不存在此售后单\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }


    /**
     * 管理员根据售后单id查询售后单信息
     * @author wxt
     */
    @Test
    @Transactional
    //平台管理员在平台范围内根据售后单id成功查询售后单
    public void getAllAftersaleByAdminIdTest1() throws Exception {
        Mockito.when(customerService.getSimpleCustomer(5L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleVo(5L,"123")));
        Mockito.when(freightService.getRegionById(1L)).thenReturn(new InternalReturnObject<>(new SimpleRegionVo(1L,"中国")));
        String responseString = this.mvc.perform(get("/shops/0/aftersales/1")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //平台管理员根据售后单id成功查询某个店铺的售后单
    public void getAllAftersaleByAdminIdTest2() throws Exception {
        Mockito.when(customerService.getSimpleCustomer(5L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleVo(5L,"123")));
        Mockito.when(freightService.getRegionById(1L)).thenReturn(new InternalReturnObject<>(new SimpleRegionVo(1L,"中国")));
        String responseString = this.mvc.perform(get("/shops/1/aftersales/1")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    //平台管理员查询不存在的售后单id
    public void getAllAftersaleByAdminIdTest3() throws Exception {
        String responseString = this.mvc.perform(get("/shops/1/aftersales/100000")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该售后单\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //平台管理员所查询的商铺不存在此售后单id
    public void getAllAftersaleByAdminIdTest4() throws Exception {
        String responseString = this.mvc.perform(get("/shops/1/aftersales/2")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":505,\"errmsg\":\"该店铺不存在此售后单\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    @Transactional
    //商铺管理员根据售后单id成功查询自己商铺中售后单
    public void getAllAftersaleByAdminIdTest5() throws Exception {
        Mockito.when(customerService.getSimpleCustomer(5L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleVo(5L,"123")));
        Mockito.when(freightService.getRegionById(1L)).thenReturn(new InternalReturnObject<>(new SimpleRegionVo(1L,"中国")));
        String responseString = this.mvc.perform(get("/shops/1/aftersales/1")
                .header("authorization", adminToken1).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":1,\"orderItemId\":1,\"customer\":{\"id\":5,\"userName\":\"123\"},\"shopId\":null,\"serviceSn\":\"1\",\"type\":1,\"reason\":null,\"price\":100,\"quantity\":1,\"region\":{\"id\":1,\"name\":\"中国\"},\"details\":null,\"consignee\":\"abc\",\"mobile\":\"12345678910\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1,\"creator\":{\"id\":0,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-11T15:04:04.000+0800\",\"gmtModified\":null,\"modifer\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString,false);
    }
    @Test
    @Transactional
    //商铺管理员查询不是自己商铺中的售后单
    public void getAllAftersaleByAdminIdTest6() throws Exception {

        String responseString = this.mvc.perform(get("/shops/2/aftersales/2")
                .header("authorization", adminToken1).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expected, responseString,true);
    }


    /**
     * 获得售后单的支付信息
     * @author wxt
     */
    @Test
    @Transactional
    //成功获得售后单的支付信息
    public void getPaymentByIdTest1() throws Exception {
        Mockito.when(paymentService.getPaymentBySn("1"))
                .thenReturn(new InternalReturnObject<>(new SimplePaymentVo(1L,"11111111111",1L,"1",(byte)4,"售后单测试",120L,110L, ZonedDateTime.parse("2021-11-11T10:10:10.000+08:00"),(byte)1,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00"),ZonedDateTime.parse("2021-11-11T11:10:10.000+08:00"))));
        String responseString = mvc.perform(get("/aftersales/1/payments")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = " {\"errno\":0,\"data\":{\"id\":1,\"tradeSn\":\"11111111111\",\"patternId\":1,\"documentId\":\"1\",\"documentType\":4,\"descr\":\"售后单测试\",\"amount\":120,\"actualAmount\":110,\"payTime\":\"2021-11-11T10:10:10.000+0800\",\"state\":1,\"beginTime\":\"2021-11-11T10:10:08.000+0800\",\"endTime\":\"2021-11-11T11:10:10.000+0800\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString,true);
    }

    @Test
    @Transactional
    //所获得的支付信息不是售后单的
    public void getPaymentByIdTest2() throws Exception {
        Mockito.when(paymentService.getPaymentBySn("1"))
                .thenReturn(new InternalReturnObject<>(new SimplePaymentVo(1L,"11111111111",1L,"123",(byte)1,"售后单测试",120L,110L,ZonedDateTime.parse("2021-11-11T10:10:10.000+08:00"),(byte)1,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00"),ZonedDateTime.parse("2021-11-01T10:10:10.000+08:00"))));
        String responseString = this.mvc.perform(get("/aftersales/1/payments")
                .header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":505,\"errmsg\":\"该支付单的交易类型不是售后\"}";
        JSONAssert.assertEquals(expected, responseString,true);
    }

    /**提交售后单
     *
     */
    @Test
    @Transactional
    public void submitAftersale() throws Exception {
        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(1L,1L,5L,400,1L,1L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(1L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));
        String json = "{\"type\":\"1\",\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/1/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"data\":{\"type\":1,\"reason\":\"坏了\",\"price\":null,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /**提交售后单，前端数据不合法
     *
     */
    @Test
    @Transactional
    public void submitAftersaleWithIllegalVo() throws Exception {
        String json = "{\"type\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/1/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":503,\"errmsg\":\"数量不能为空;\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**提交售后单，订单明细id不存在
     *
     */
    @Test
    @Transactional
    public void submitAftersaleNotExist() throws Exception {
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(999L))
                .thenReturn(new InternalReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(),"订单明细id不存在"));
        String json = "{\"type\":\"1\",\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/999/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"订单明细id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /**提交售后单，订单中顾客id与提交售后单顾客id不一致
     *
     */
    @Test
    @Transactional
    public void submitAftersaleWithWrongCustomer() throws Exception {
        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(1L,1L,6L,400,1L,1L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(2L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));
        String json = "{\"type\":\"1\",\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/2/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"无法建立不属于自己的订单的售后单\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**提交售后单，订单未发货
     *
     */
    @Test
    @Transactional
    public void submitAftersaleWithOrderNotDelivered() throws Exception {
        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(3L,3L,5L,203,1L,1L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(3L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));
        String json = "{\"type\":\"1\",\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/3/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**提交售后单，订单已取消
     *
     */
    @Test
    @Transactional
    public void submitAftersaleWithOrderCancelled() throws Exception {
        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(3L,3L,5L,203,1L,1L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(4L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));
        String json = "{\"type\":\"1\",\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(post("/orderitems/4/aftersales")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /** 买家修改售后单信息（店家发货前）
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void updateAftersale() throws Exception {
        String json = "{\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(put("/aftersales/1")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家修改不存在的售后单信息（店家发货前）
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void updateAftersaleNotExist() throws Exception {
        String json = "{\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(put("/aftersales/999")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家修改售后单信息（店家发货后）
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void updateAftersaleAfterDeliver() throws Exception {
        String json = "{\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(put("/aftersales/2")
                .header("authorization", customerTokenWithIdSix)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家修改不属于自己的售后单信息
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void updateAftersaleOutOfScope() throws Exception {
        String json = "{\"quantity\":\"1\",\"reason\":\"坏了\",\"regionId\":\"1\",\"detail\":\"厦门大学\",\"consignee\":\"先生\",\"mobile\":\"123\"}";
        String responseString = this.mvc.perform(put("/aftersales/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"没有修改权限\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 买家取消售后单
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void deleteAftersale() throws Exception {
        String responseString = this.mvc.perform(delete("/aftersales/1")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家取消不存在的售后单
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void deleteAftersaleNotExist() throws Exception {
        String responseString = this.mvc.perform(delete("/aftersales/999")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家逻辑删除售后单
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void deleteAftersaleByLogic() throws Exception {
        String responseString = this.mvc.perform(delete("/aftersales/3")
                .header("authorization", customerTokenWithIdSix)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家取消售后单和逻辑删除不属于自己的售后单
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void deleteAftersaleOutOfScope() throws Exception {
        String responseString = this.mvc.perform(delete("/aftersales/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"没有删除权限\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 买家填写售后单的运单信息
     *
     * @Param id 售后单id
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void fillCustomerLogSn() throws Exception {
        String json = "{\"logSn\":\"123456\"}";
        String responseString = this.mvc.perform(put("/aftersales/1/sendback")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家填写不存在的售后单的运单信息
     *
     * @Param id 售后单id
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void fillCustomerLogSnNotExist() throws Exception {
        String json = "{\"logSn\":\"123456\"}";
        String responseString = this.mvc.perform(put("/aftersales/999/sendback")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 买家填写不处于待买家发货状态的售后单的运单信息
     * @Param id 售后单id
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void fillCustomerLogSnIllegalState() throws Exception {
        String json = "{\"logSn\":\"123456\"}";
        String responseString = this.mvc.perform(put("/aftersales/2/sendback")
                .header("authorization", customerTokenWithIdSix)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家填写不属于自己的售后单的运单信息
     *
     * @Param id 售后单id
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void fillCustomerLogSnOutOfScope() throws Exception {
        String json = "{\"logSn\":\"123456\"}";
        String responseString = this.mvc.perform(put("/aftersales/1/sendback")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"没有修改权限\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家填写售后单的运单信息，传入的vo不合法
     * @Param id 售后单id
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void fillCustomerLogSnWithIllegalVo() throws Exception {
        String json = "{}";
        String responseString = this.mvc.perform(put("/aftersales/1/sendback")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":503,\"errmsg\":\"运单号不能为空;\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 买家确认售后单结束
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByCustomer() throws Exception {
        String responseString = this.mvc.perform(put("/aftersales/2/confirm")
                .header("authorization", customerTokenWithIdSix)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家确认不处于店家已发货状态的售后单结束
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByCustomerWithIllegalState() throws Exception {
        String responseString = this.mvc.perform(put("/aftersales/1/confirm")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家确认不存在的售后单结束
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByCustomerNotExist() throws Exception {
        String responseString = this.mvc.perform(put("/aftersales/999/confirm")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 买家确认不属于自己的售后单结束
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByCustomerOutOfScope() throws Exception {
        String responseString = this.mvc.perform(put("/aftersales/2/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"没有修改权限\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 管理员同意（退款，换货，维修）
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByAdmin() throws Exception {
        String json = "{\"confirm\":\"true\",\"price\":\"10\",\"conclusion\":\"同意\",\"type\":\"1\"}";
        String responseString = this.mvc.perform(put("/shops/4/aftersales/4/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 管理员不同意（退款，换货，维修）
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void NotConfirmAftersaleByAdmin() throws Exception {
        String json = "{\"confirm\":\"false\"}";
        String responseString = this.mvc.perform(put("/shops/4/aftersales/4/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 管理员同意/不同意（退款，换货，维修），传入的vo不合法
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByAdminWithIllegalVo() throws Exception {
        String json = "{\"price\":\"10\",\"conclusion\":\"同意\",\"type\":\"1\"}";
        String responseString = this.mvc.perform(put("/shops/4/aftersales/4/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":503,\"errmsg\":\"处理结果不能为空;\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    /** 管理员同意（退款，换货，维修），售后单id不存在
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByAdminNotExist() throws Exception {
        String json = "{\"confirm\":\"true\",\"price\":\"10\",\"conclusion\":\"同意\",\"type\":\"1\"}";
        String responseString = this.mvc.perform(put("/shops/4/aftersales/999/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"售后单id不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 管理员同意（退款，换货，维修），售后单id与店铺id不匹配
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByAdminShopIdNotMatched() throws Exception {
        String json = "{\"confirm\":\"true\",\"price\":\"10\",\"conclusion\":\"同意\",\"type\":\"1\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/4/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"售后单id与店铺id不匹配\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /** 管理员同意（退款，换货，维修），售后单状态为非新建态
     *
     * @author 张晖婧
     */
    @Test
    @Transactional
    public void confirmAftersaleByAdminStateNotAllowed() throws Exception {
        String json = "{\"confirm\":\"true\",\"price\":\"10\",\"conclusion\":\"同意\",\"type\":\"1\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/1/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /**
     * 店家验收通过买家的货（退货）
     */
    @Test
    @Transactional
    public void confirmRecieveByShopForReturn() throws Exception {

        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(1L,1L,5L,400,1L,6L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(6L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));
        AftersaleRefundVo aftersaleRefundVo = new AftersaleRefundVo("6", 6L, 800L, 20L);
        Mockito.when(paymentService.refundForAftersale(1L, 5L, aftersaleRefundVo))
                .thenReturn(new InternalReturnObject(ReturnNo.OK.getCode(),"退款成功"));

        String json = "{\"confirm\":\"true\",\"conclusion\":\"同意退款\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/6/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**
     * 店家验收通过买家的货（换货）
     */
    @Test
    @Transactional
    public void confirmRecieveByShopForExchange() throws Exception {

        OrderItemForAftersaleVo orderItemForAftersaleVo=new OrderItemForAftersaleVo(1L,1L,5L,400,1L,7L,1L,"苹果",1,1000L,200L,20L);
        Mockito.when(orderService.getOrderItemForAftersaleByOrderItemId(7L))
                .thenReturn(new InternalReturnObject<>(orderItemForAftersaleVo));

        OrderItemForExchangeVo orderItemForExchangeVo=new OrderItemForExchangeVo(1L,"苹果",1L);
        OrderInfoForExchangeVo orderInfoForExchangeVo=new OrderInfoForExchangeVo(orderItemForExchangeVo,5L,"abc",1L,"厦门大学","12345678910","换货订单");

        AftersaleRefundVo aftersaleRefundVo = new AftersaleRefundVo("7", 7L, 800L, 20L);
        Mockito.when(orderService.submitExchangeOrderForAftersale(1L, orderInfoForExchangeVo))
                .thenReturn(new InternalReturnObject(ReturnNo.OK.getCode(),"订单建立成功"));

        String json = "{\"confirm\":\"true\",\"conclusion\":\"同意换货\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/7/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**
     * 店家验收通过买家的货（维修）
     */
    @Test
    @Transactional
    public void confirmRecieveByShopForRepair() throws Exception {
        SimplePaymentVo simplePaymentVo=new SimplePaymentVo(1L,"1",1L,"11",(byte)0,"订单付款",1000L,800L,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df),(byte)1,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df),ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df));
        Mockito.when(paymentService.getPayment(8L))
                .thenReturn(new ReturnObject<>(simplePaymentVo));

        PaymentVo paymentVo=new PaymentVo(1L,"8",(byte)4,"维修费用",2000L,ZonedDateTime.now(),ZonedDateTime.now().plusHours(24));

        Mockito.when(paymentService.setCustomerPayment(Mockito.anyLong(),Mockito.anyLong(), Mockito.any()))
                .thenReturn(new InternalReturnObject(ReturnNo.OK.getCode(),"订单建立成功"));

        String json = "{\"confirm\":\"true\",\"conclusion\":\"同意维修\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/8/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**
     * 店家验收通过买家的货（退货），售后单id与店铺id不匹配
     */
    @Test
    @Transactional
    public void confirmRecieveByShopForReturnNotExist() throws Exception {
        String json = "{\"confirm\":\"true\",\"conclusion\":\"同意退款\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/999/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"店铺不存在该售后单\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /**
     * 店家验收通过买家的货（退货），售后单状态不是买家已发货
     */
    @Test
    @Transactional
    public void confirmRecieveByShopForReturnStateNotAllowed() throws Exception {
        String json = "{\"confirm\":\"true\",\"conclusion\":\"同意退款\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/1/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    /**
     * 店家验收不通过通过买家的货（退货）
     */
    @Test
    @Transactional
    public void confirmNotRecieveByShopForReturn() throws Exception {
        String json = "{\"confirm\":\"false\",\"conclusion\":\"商品已耗损\"}";
        String responseString = this.mvc.perform(put("/shops/1/aftersales/6/receive")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /**
     * 店家发货
     */
    @Test
    @Transactional
    public void fillShopLogSn() throws Exception {
        String json = "{\"shopLogSn\":\"456789\"}";
        String responseString = this.mvc.perform(put("/shops/5/aftersales/5/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /**
     * 店家发货，售后单id与店铺id不匹配
     */
    @Test
    @Transactional
    public void fillShopLogSnNotExist() throws Exception {
        String json = "{\"shopLogSn\":\"456789\"}";
        String responseString = this.mvc.perform(put("/shops/6/aftersales/5/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":504,\"errmsg\":\"店铺不存在该售后单\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /**
     * 店家发货，售后单状态不是待店家发货
     */
    @Test
    @Transactional
    public void fillShopLogSnStateNotAllowed() throws Exception {
        String json = "{\"shopLogSn\":\"456789\"}";
        String responseString = this.mvc.perform(put("/shops/4/aftersales/4/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
    /**
     * 测试rocketMQ
     */
    @Rollback()
    @Test
    @Transactional
    public void paymentRocketMQTest()throws Exception{
        SimplePaymentVo simplePaymentVo=new SimplePaymentVo(9L,"9",1L,"9",(byte)4,"维修支付",800L,800L,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df),(byte)1,ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df),ZonedDateTime.parse("2021-11-11T10:10:08.000+08:00",df));
        Object object=simplePaymentVo;
        String json=JSON.toJSONString(object);
//        Message message = MessageBuilder.withPayload(object).build();
//        logger.info("sendLogMessage: message = " + message);
//        rocketMQTemplate.sendOneWay("payment-success-topic", message);
        aftersalePaymentListener.onMessage(json);

    }

    /* ------------------------------ 内部 API ------------------------------ */

    @Test
    @Transactional
    public void getAftersaleBySnTest()throws Exception {
        String sn="1";
        Mockito.when(customerService.getSimpleCustomer(5L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleVo(5L,"123")));
        Mockito.when(freightService.getRegionById(1L)).thenReturn(new InternalReturnObject<>(new SimpleRegionVo(1L,"中国")));
        String responseString = this.mvc.perform(get("/internal/aftersale?aftersaleSn=1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = " {\"errno\":0,\"data\":{\"id\":1,\"orderId\":1,\"orderItemId\":1,\"customer\":{\"id\":5,\"userName\":\"123\"},\"shopId\":null,\"serviceSn\":\"1\",\"type\":1,\"reason\":null,\"price\":100,\"quantity\":1,\"region\":{\"id\":1,\"name\":\"中国\"},\"details\":null,\"consignee\":\"abc\",\"mobile\":\"12345678910\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString,true);
    }
}
