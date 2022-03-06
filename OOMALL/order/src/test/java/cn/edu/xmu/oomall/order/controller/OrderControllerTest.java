package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.OrderApplication;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.microservice.*;
import cn.edu.xmu.oomall.order.microservice.vo.*;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.RocketMqService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


/**
 * @author RenJieZheng 22920192204334
 */
@Transactional      //防止脏数据
@SpringBootTest(classes = OrderApplication.class)
@AutoConfigureMockMvc      //自动初始化MockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest {
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private MockMvc mvc;

    @Resource
    OrderService orderService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.ActivityService")
    private ActivityService activityService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.CouponService")
    private CouponService couponService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.GoodsService")
    private GoodsService goodsService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.FreightService")
    private FreightService freightService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.ShopService")
    private ShopService shopService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.CustomerService")
    private CustomerService customerService;

    @MockBean(name = "cn.edu.xmu.oomall.order.microservice.PaymentService")
    private PaymentService paymentService;

    @MockBean
    RedisUtil redisUtil;

    @MockBean
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    @MockBean
    RocketMqService rocketMqService;

    static String json1 = "{\"id\":1,\"customerId\":1,\"shopId\":1,\"orderSn\":\"1234567890\",\"pid\":0,\"consignee\":\"zrj\",\"regionId\":1,\"address\":\"北京市\",\"mobile\":\"13495671234\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"expressFee\":2000,\"discountPrice\":1000,\"originPrice\":20000,\"point\":1000,\"confirmTime\":[2021,12,3,21,15,47],\"shipmentSn\":\"123456789\",\"state\":100,\"beDeleted\":0,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":[2021,12,3,21,16,28],\"gmtModified\":[2021,12,3,21,16,28]}";
    static String json2 = "{\"id\":2,\"customerId\":1,\"shopId\":1,\"orderSn\":\"1234567891\",\"pid\":0,\"consignee\":\"zrj\",\"regionId\":1,\"address\":\"北京市\",\"mobile\":\"13495671234\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"expressFee\":2000,\"discountPrice\":1000,\"originPrice\":20000,\"point\":1000,\"confirmTime\":[2021,12,3,21,15,47],\"shipmentSn\":\"123456789\",\"state\":201,\"beDeleted\":0,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":[2021,12,3,23,16,28],\"gmtModified\":[2021,12,3,21,16,28]}";
    static String json3 = "{\"id\":3,\"customerId\":1,\"shopId\":1,\"orderSn\":\"1234567892\",\"pid\":0,\"consignee\":\"zrj\",\"regionId\":1,\"address\":\"北京市\",\"mobile\":\"13495671234\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"expressFee\":2000,\"discountPrice\":1000,\"originPrice\":20000,\"point\":1000,\"confirmTime\":[2021,12,4,20,48,2],\"shipmentSn\":\"123456789\",\"state\":300,\"beDeleted\":0,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":[2021,12,4,20,48,40],\"gmtModified\":[2021,12,4,20,48,42]}";
    static String json4 = "{\"id\":4,\"customerId\":1,\"shopId\":1,\"orderSn\":\"1231231123\",\"pid\":0,\"consignee\":\"zrj\",\"regionId\":1,\"address\":\"北京市\",\"mobile\":\"12312341231\",\"message\":null,\"advancesaleId\":null,\"grouponId\":1,\"expressFee\":2000,\"discountPrice\":1000,\"originPrice\":20000,\"point\":1000,\"confirmTime\":[2021,12,5,10,45,24],\"shipmentSn\":\"123412341\",\"state\":202,\"beDeleted\":0,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":[2021,12,5,10,45,51],\"gmtModified\":[2021,12,5,10,45,53]}";
    static String json5 = "{\"id\":5,\"customerId\":1,\"shopId\":1,\"orderSn\":\"1231231231\",\"pid\":0,\"consignee\":\"zrj\",\"regionId\":1,\"address\":\"北京市\",\"mobile\":\"13212312312\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"expressFee\":2000,\"discountPrice\":1000,\"originPrice\":20000,\"point\":1000,\"confirmTime\":[2021,12,18,9,24,10],\"shipmentSn\":\"123412341\",\"state\":400,\"beDeleted\":0,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":[2021,12,18,9,24,36],\"gmtModified\":[2021,12,18,9,24,37]}";

    @Test
    public void testShowAllState()throws Exception{
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/states")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"data\":[{\"code\":100,\"name\":\"待付款\"},{\"code\":200,\"name\":\"待收货\"},{\"code\":300,\"name\":\"已发货\"},{\"code\":400,\"name\":\"已完成\"},{\"code\":500,\"name\":\"已取消\"},{\"code\":501,\"name\":\"待退款\"},{\"code\":502,\"name\":\"已退款\"},{\"code\":101,\"name\":\"新订单\"},{\"code\":102,\"name\":\"待支付尾款\"},{\"code\":201,\"name\":\"付款完成\"},{\"code\":202,\"name\":\"待成团\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testShowCustomerOwnOrderInfo() throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders")
                .param("page","1").param("pageSize","10").param("state","100")
                .param("beginTime","2020-10-11T12:00:00.235+08:00")
                .param("endTime","2027-10-11T12:00:00.235+08:00")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testCustomerPostNewNormalOrder() throws Exception{
        //普通订单
        OrderItemVo orderItemVo1 = new OrderItemVo(1L,1L,1L,1L,1L);
        List<OrderItemVo>orderItemVos = Arrays.asList(orderItemVo1);
        OrderInfoVo orderInfo = new OrderInfoVo(orderItemVos,"zrj",1L,null,"17812312312",null,null,null,100L,null);
        String json = JacksonUtil.toJson(orderInfo);

        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        OnSaleRetVo onSaleRetVo = new OnSaleRetVo();
        onSaleRetVo.setId(1L);
        onSaleRetVo.setQuantity(1000000);
        onSaleRetVo.setShop(new SimpleShopVo(1L,"123"));
        onSaleRetVo.setPrice(100L);
        onSaleRetVo.setType((byte)1);
        Mockito.when(customerService.useCoupon(Mockito.any())).thenReturn(new InternalReturnObject());
        Mockito.when(activityService.isAdvanceSaleExist(null)).thenReturn(new InternalReturnObject<>(false));
        Mockito.when(activityService.isGrouponExist(null)).thenReturn(new InternalReturnObject<>(false));
        Mockito.when(couponService.isCouponExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(couponService.isCouponActivityExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(goodsService.isOnsaleExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(goodsService.isProductExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(goodsService.selectFullOnsale(1L)).thenReturn(new InternalReturnObject<>(onSaleRetVo));
        Mockito.when(customerService.getPointByUserId(1L)).thenReturn(new InternalReturnObject<>(new CustomerPointRetVo(1L,100L)));
        Mockito.when(freightService.isRegionExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(redisUtil.hasKey("o_1")).thenReturn(false);
        Mockito.when(goodsService.decreaseOnSale(1L,1L,new QuantityVo(1))).thenReturn(new InternalReturnObject());
        List<DiscountItemVo>list = Arrays.asList(new DiscountItemVo(1L,1L,1L,100L,1L));
        List<DiscountRetVo>list1 = Arrays.asList(new DiscountRetVo(1L,1L,90L,1L));
        Mockito.when(couponService.calculateDiscount(list)).thenReturn(new InternalReturnObject<>(list1));
        ProductRetVo productRetVo = new ProductRetVo();
        productRetVo.setId(1L);
        productRetVo.setWeight(100L);
        productRetVo.setFreightId(1L);
        Mockito.when(goodsService.getProductDetails(1L)).thenReturn(new InternalReturnObject<>(productRetVo));
        List<FreightCalculatingPostVo> postVos = Arrays.asList(new FreightCalculatingPostVo(1L,1,1L,100));
        Mockito.when(freightService.calculateFreight(1L,postVos)).thenReturn(new InternalReturnObject<>(new FreightCalculatingRetVo(1L,1L)));
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\": 0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        //团购订单
        Mockito.when(activityService.isGrouponExist(1L)).thenReturn(new InternalReturnObject<>(true));

        OrderItemVo orderItemVo2 = new OrderItemVo(1L,1L,1L,1L,1L);
        List<OrderItemVo>orderItemVos1= Arrays.asList(orderItemVo2);
        OrderInfoVo orderInfo1 = new OrderInfoVo(orderItemVos1,"zrj",1L,null,"17812312312",null,null,1L,100L,null);
        String json1 = JacksonUtil.toJson(orderInfo1);
        String responseString1;
        responseString1 = this.mvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(json1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        //预售订单
        Mockito.when(activityService.isAdvanceSaleExist(1L)).thenReturn(new InternalReturnObject<>(true));
        AdvanceSaleRetVo advanceSaleRetVo = new AdvanceSaleRetVo();
        advanceSaleRetVo.setId(1L);
        advanceSaleRetVo.setAdvancePayPrice(60L);
        advanceSaleRetVo.setPrice(100L);
        Mockito.when(activityService.queryOnlineAdvanceSaleInfo(1L)).thenReturn(new InternalReturnObject<>(advanceSaleRetVo));
        OrderItemVo orderItemVo3 = new OrderItemVo(1L,1L,1L,1L,1L);
        List<OrderItemVo>orderItemVos3= Arrays.asList(orderItemVo3);
        OrderInfoVo orderInfo3 = new OrderInfoVo(orderItemVos3,"zrj",1L,null,"17812312312",null,1L,null,100L,null);
        String json3 = JacksonUtil.toJson(orderInfo3);
        String responseString3;
        responseString3 = this.mvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(json3))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);
    }

    @Test
    public void testShowOrderInfoById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        Mockito.when(shopService.getShopById(1L)).thenReturn(new InternalReturnObject<>(new SimpleObjectRetVo(1L,"不知道是什么店")));
        Mockito.when(customerService.getSimpleCustomer(1L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleRetVo(1L,"张三")));
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/1")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);


        String adminToken3 = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/orders/100000")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken3))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);


    }

    @Test
    public void testUpdateOrderUsingField()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        List<FreightCalculatingPostVo> postVos = Arrays.asList(new FreightCalculatingPostVo(1L,5,1L,100));
        Mockito.when(freightService.calculateFreight(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(new FreightCalculatingRetVo(2000L,1L)));
        ProductRetVo productRetVo = new ProductRetVo();
        productRetVo.setId(1L);
        productRetVo.setWeight(100L);
        productRetVo.setFreightId(1L);
        Mockito.when(goodsService.getProductDetails(Mockito.any())).thenReturn(new InternalReturnObject<>(productRetVo));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        OrderUpdateFieldVo orderUpdateFieldVo = new OrderUpdateFieldVo("张三",1L,"1L","17813212312");
        String json = JacksonUtil.toJson(orderUpdateFieldVo);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/orders/1")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/100")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        Mockito.when(freightService.calculateFreight(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(new FreightCalculatingRetVo(100L,1L)));
        ProductRetVo productRetVo1 = new ProductRetVo();
        productRetVo1.setId(1L);
        productRetVo1.setWeight(100L);
        productRetVo1.setFreightId(1L);
        Mockito.when(goodsService.getProductDetails(1L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/1")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 801,\n" +
                "\"errmsg\": \"订单地址费用变化\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);
    }

    @Test
    public void testDeleteOrderById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(5L)).thenReturn(JacksonUtil.toObj(json5,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/orders/5")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.delete("/orders/100")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);
    }

    @Test
    public void testCancelOrderById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(2L)).thenReturn(JacksonUtil.toObj(json2,OrderPo.class));
        //进行退款
        RefundVo refundVo = new RefundVo("1234567890",(byte)0,null);
        Mockito.when(paymentService.refund(refundVo)).thenReturn(new InternalReturnObject<>());
        Mockito.when(goodsService.increaseOnSale(1L,1L,new QuantityVo(5))).thenReturn(new InternalReturnObject<>());
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/orders/2/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        String adminToken1 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/1/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/100/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(5L)).thenReturn(JacksonUtil.toObj(json5,OrderPo.class));
        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/5/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);

    }

    @Test
    public void testConfirmOrderById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(3L)).thenReturn(JacksonUtil.toObj(json3,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/orders/3/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        String adminToken1 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/1/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/100/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(2L)).thenReturn(JacksonUtil.toObj(json2,OrderPo.class));
        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.put("/orders/2/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);
    }

    @Test
    public void testShowShopOwnOrderInfo()throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders")
                .param("page","1").param("pageSize","10")
                .param("customerId","1").param("beginTime","2020-10-11T11:10:11.235+08:00")
                .param("endTime","2023-10-11T11:10:11.235+08:00")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testUpdateOrderMessageById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        MessageVo messageVo = new MessageVo("1212312321312");
        String json = JacksonUtil.toJson(messageVo);
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/1")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String adminToken1 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/1")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/100")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/100/orders/1")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);
    }

    @Test
    public void testShowShopOwnOrderInfoById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        Mockito.when(shopService.getShopById(1L)).thenReturn(new InternalReturnObject<>(new SimpleObjectRetVo(1L,"不知道是什么店")));
        Mockito.when(customerService.getSimpleCustomer(1L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleRetVo(1L,"张三")));
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders/1")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String adminToken3 = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders/100")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken3))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);
    }

    @Test
    public void testDeleteShopOrderById()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(2L)).thenReturn(JacksonUtil.toObj(json2,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "699275", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/1/orders/2")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String adminToken1 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/1/orders/2")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/1/orders/1000000")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/1/orders/2")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);

        String adminToken4 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString4 = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/100/orders/2")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken4))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString4 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString4,responseString4,false);

    }

    @Test
    public void testPostFreights()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(2L)).thenReturn(JacksonUtil.toObj(json2,OrderPo.class));
        FreightSnVo freightSnVo = new FreightSnVo("1212312321312");
        String json = JacksonUtil.toJson(freightSnVo);
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/2/deliver")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String adminToken1 = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/2/deliver")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/100/deliver")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(3L)).thenReturn(JacksonUtil.toObj(json3,OrderPo.class));
        String responseString4 = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/3/deliver")
                .content(json)
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString4 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString4,responseString4,false);
    }

    @Test
    public void testShowOwnPayment() throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        PaymentSimpleRetVo paymentSimpleRetVo = new PaymentSimpleRetVo();
        paymentSimpleRetVo.setId(1L);
        paymentSimpleRetVo.setDocumentId("1234567890");
        List<PaymentSimpleRetVo> list = Arrays.asList(paymentSimpleRetVo);
        Mockito.when(paymentService.getPaymentsByOrderSn("1234567890")).thenReturn(new InternalReturnObject<>(list));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/1/payment")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);


        String adminToken3 = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/orders/100/payment")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken3))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

    }

    @Test
    public void testShowOwnRefund() throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(1L)).thenReturn(JacksonUtil.toObj(json1,OrderPo.class));
        RefundSimpleRetVo refundSimpleRetVo = new RefundSimpleRetVo();
        refundSimpleRetVo.setId(1L);
        refundSimpleRetVo.setDocumentId("1234567890");
        List<RefundSimpleRetVo> list = Arrays.asList(refundSimpleRetVo);
        Mockito.when(paymentService.getRefundsByOrderSn("1234567890")).thenReturn(new InternalReturnObject<>(list));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/1/refund")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String adminToken1 = jwtHelper.createToken(60L, "pikaas", 0L, 1, 3600);
        String responseString1 = this.mvc.perform(MockMvcRequestBuilders.get("/orders/1/refund")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        String adminToken3 = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/orders/100/refund")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken3))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

    }


    @Test
    public void testConfirmGrouponOrder()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(4L)).thenReturn(JacksonUtil.toObj(json4,OrderPo.class));
        GroupOnStrategyVo groupOnStrategyVo1 = new GroupOnStrategyVo(1,900);
        GroupOnStrategyVo groupOnStrategyVo2 = new GroupOnStrategyVo(10,600);
        GroupOnStrategyVo groupOnStrategyVo3 = new GroupOnStrategyVo(500,300);
        List<GroupOnStrategyVo>list = Arrays.asList(groupOnStrategyVo1,groupOnStrategyVo2,groupOnStrategyVo3);
        GroupOnActivityVo groupOnActivityVo = new GroupOnActivityVo(1L,"fsf",new ShopVo(1L,"sdfa"),list,null,null);
        Mockito.when(activityService.getOnlineGroupOnActivity(1L)).thenReturn(new InternalReturnObject<>(groupOnActivityVo));
        RefundVo refundVo = new RefundVo("1234567890",(byte)0L,300L);
        Mockito.when(paymentService.refund(refundVo)).thenReturn(new InternalReturnObject<>());
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/internal/shops/1/grouponorders/4/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/internal/shops/1/grouponorders/100/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        Mockito.when(orderPoMapper.selectByPrimaryKey(2L)).thenReturn(JacksonUtil.toObj(json2,OrderPo.class));
        String responseString4 = this.mvc.perform(MockMvcRequestBuilders.put("/internal/shops/1/grouponorders/2/confirm")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString4 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString4,responseString4,false);
    }

    @Test
    public void testCancelOrder()throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(4L)).thenReturn(JacksonUtil.toObj(json4,OrderPo.class));
        //进行退款
        RefundVo refundVo = new RefundVo("1234567890",(byte)0,null);
        Mockito.when(paymentService.refund(refundVo)).thenReturn(new InternalReturnObject<>());
        Mockito.when(goodsService.increaseOnSale(1L,1L,new QuantityVo(5))).thenReturn(new InternalReturnObject<>());
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/internal/shops/1/orders/4/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/internal/shops/1/orders/100000/cancel")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);
    }

    @Test
    public void testAdminPostAfterSaleOrder() throws Exception{
        //普通订单
        OrderItemSimpleVo orderItemVo1 = new OrderItemSimpleVo(1L,1L,1L);
        List<OrderItemSimpleVo>orderItemVos = Arrays.asList(orderItemVo1);
        AfterSaleOrderInfo orderInfo = new AfterSaleOrderInfo(orderItemVos,1L,"zrj",1L,null,"12312312312","1232");
        String json = JacksonUtil.toJson(orderInfo);
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        OnSaleRetVo onSaleRetVo = new OnSaleRetVo();
        onSaleRetVo.setId(1L);
        onSaleRetVo.setQuantity(1000000);
        onSaleRetVo.setShop(new SimpleShopVo(1L,"123"));
        onSaleRetVo.setPrice(100L);
        onSaleRetVo.setType((byte)1);
        Mockito.when(goodsService.selectFullOnsale(1L)).thenReturn(new InternalReturnObject<>(onSaleRetVo));
        Mockito.when(freightService.isRegionExist(1L)).thenReturn(new InternalReturnObject<>(true));
        Mockito.when(redisUtil.hasKey("o_1")).thenReturn(false);
        Mockito.when(goodsService.decreaseOnSale(1L,1L,new QuantityVo(1))).thenReturn(new InternalReturnObject());
        ProductRetVo productRetVo = new ProductRetVo();
        productRetVo.setId(1L);
        productRetVo.setWeight(100L);
        productRetVo.setFreightId(1L);
        Mockito.when(goodsService.getProductDetails(1L)).thenReturn(new InternalReturnObject<>(productRetVo));
        List<FreightCalculatingPostVo> postVos = Arrays.asList(new FreightCalculatingPostVo(1L,1,1L,100));
        Mockito.when(freightService.calculateFreight(1L,postVos)).thenReturn(new InternalReturnObject<>(new FreightCalculatingRetVo(1L,1L)));
        Mockito.when(shopService.getShopById(1L)).thenReturn(new InternalReturnObject<>(new SimpleObjectRetVo(1L,"不知道是什么店")));
        Mockito.when(customerService.getSimpleCustomer(1L)).thenReturn(new InternalReturnObject<>(new CustomerSimpleRetVo(1L,"张三")));
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/internal/shops/1/orders")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }



    @Test
    public void testShowOrderItemsByOrderItemId() throws Exception{
        Mockito.when(orderPoMapper.selectByPrimaryKey(Mockito.any())).thenReturn(JacksonUtil.toObj(json4,OrderPo.class));
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/internal/orderitems/1")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testShowOrderByOrderSn()throws Exception{

        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/internal/order")
                .param("orderSn","1234567890")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testShowOrderItemByOrderId()throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/internal/orderitem")
                .param("orderId","1").param("page","1").param("pageSize","10")
                .contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testSeperateOrdersByOrderSn()throws Exception{
        orderService.seperateOrdersByOrderSn("1234567890",(byte)0);
    }
}
