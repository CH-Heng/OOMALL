package cn.edu.xmu.oomall.liquidation.controller;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.liquidation.LiquidationApplication;
import cn.edu.xmu.oomall.liquidation.microservice.*;
import cn.edu.xmu.oomall.liquidation.microservice.vo.AftersaleRetVo;
import cn.edu.xmu.oomall.liquidation.microservice.vo.CustomerPointVo;
import cn.edu.xmu.oomall.liquidation.microservice.vo.RefundRetVo;
import cn.edu.xmu.oomall.liquidation.util.*;
import cn.edu.xmu.oomall.liquidation.util.base.ListFactory;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("classpath:application.yaml")
@SpringBootTest(classes = LiquidationApplication.class)
public class LiquidationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    private String adminToken;
    private String customerToken1;
    private String customerToken2;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private ShareService shareService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AftersaleService aftersaleService;

    private OrderFactory orderFactory = new OrderFactory();

    private OrderItemFactory orderItemFactory = new OrderItemFactory();

    private PaymentFactory paymentFactory = new PaymentFactory();

    private ShopFactory shopFactory = new ShopFactory();

    private ProductFactory productFactory = new ProductFactory();

    private SuccessfulShareFactory successfulShareFactory = new SuccessfulShareFactory();

    @BeforeEach
    void init() {
        JwtHelper jwt = new JwtHelper();
        adminToken = jwt.createToken(0L, "admin", 0L, 1, 3600);
        customerToken1 = jwt.createToken(10000L, "wwk", -1L, -1, 3600);
        customerToken2 = jwt.createToken(10000235L,"wwk's father",-1L, -1, 3600);
    }

    @Test
    @Transactional
    public void getStatesTest() throws Exception {
        String responseString = this.mvc.perform(get("/liquidation/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"未汇出\"},{\"code\":1,\"name\":\"已汇出\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getSimpleLiquidationTest() throws Exception {
        String responseString = this.mvc.perform(get("/shops/10000123/liquidation?beginDate=1991-09-30T15:01:02.000+08:00&endDate=1992-10-02T15:01:02.000+08:00&state=0")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10000123,\"shop\":{\"id\":10000123,\"name\":\"不要删这个\"},\"liquidDate\":\"1991-12-18T08:59:46.000+0800\",\"expressFee\":123,\"commission\":123,\"point\":123,\"state\":0}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getDetailLiquidationTest() throws Exception {
        String responseString = this.mvc.perform(get("/shops/10000123/liquidation/10000123")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"id\":10000123,\"shop\":{\"id\":10000123,\"name\":\"不要删这个\"},\"liquidDate\":\"1991-12-18T08:59:46.000+0800\",\"expressFee\":123,\"commission\":123,\"shopRevenue\":123,\"point\":123,\"state\":0,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"2021-12-17T18:56:39.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifier\":{\"id\":10000456,\"name\":\"asda\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getRevenueTest() throws Exception {
        String responseString = this.mvc.perform(get("/shops/10000123/revenue?orderId=10000222&productId=10000333")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10000234,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:24:59.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null},{\"id\":10000235,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:25:06.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getExpenditureTest() throws Exception {
        String responseString = this.mvc.perform(get("/shops/10000123/expenditure?orderId=10000222&productId=10000333&liquidationId=10000123")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10000236,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:24:40.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getRevenueByExpenditureTest() throws Exception {
        String responseString = this.mvc.perform(get("/shops/10000123/expenditure/10000236/revenue")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"id\":10000234,\"shop\":{\"id\":10000123,\"name\":null},\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:24:59.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getPointRecordRevenuesTest() throws Exception {
        String responseString = this.mvc.perform(get("/pointrecords/revenue")
                .header("authorization", customerToken2)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10000235,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:25:06.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null},{\"id\":10000234,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:24:59.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void getPointRecordExpenditureTest() throws Exception {
        String responseString = this.mvc.perform(get("/pointrecords/expenditure")
                .header("authorization", customerToken2)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":10000236,\"shop\":null,\"product\":{\"id\":10000333,\"name\":\"name\"},\"amount\":123,\"quantity\":123,\"commission\":123,\"point\":123,\"shopRevenue\":236,\"expressFee\":123,\"creator\":{\"id\":10000789,\"name\":\"name\"},\"gmtCreate\":\"1991-10-18T09:24:40.000+0800\",\"gmtModified\":\"2021-12-17T18:56:41.000+0800\",\"modifierId\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    // 开始清算

    @Test
    //@Transactional
    public void startLiquidation() throws Exception {
        String json = "{\"beginTime\":\"2021-12-18T00:00:00.000+08:00\",\"endTime\":\"2021-12-18T23:23:23.000+08:00\"}";

        ZonedDateTime beginTime = ZonedDateTime.of(2021,12,17,16,0,0,0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2021,12,18,15,23,23,0, ZoneId.of("UTC"));

        Mockito.when(shopService.getShop(1, 50)).thenReturn(new InternalReturnObject(new ListFactory(shopFactory).create(3)));
        Mockito.when(shopService.getShop(2, 50)).thenReturn(new InternalReturnObject(new ListFactory(shopFactory).create(0)));

        Mockito.when(paymentService.getPayment(0L, beginTime, endTime, 1, 50)).thenReturn(new ReturnObject(new ListFactory(paymentFactory).create(1)));
        Mockito.when(paymentService.getPayment(0L, beginTime, endTime, 2, 50)).thenReturn(new ReturnObject(new ListFactory(paymentFactory).create(0)));

        //售后退款
        RefundRetVo refundRetVo=new RefundRetVo();
        refundRetVo.setId(666L);
        refundRetVo.setPaymentId(1L);
        refundRetVo.setDocumentId("aftersalesn1");
        refundRetVo.setState((byte)2);
        refundRetVo.setDocumentType((byte)2);

        //订单退款
        RefundRetVo refundRetVo1=new RefundRetVo();
        refundRetVo1.setPaymentId(1L);
        refundRetVo1.setDocumentId("aftersalesn2");
        refundRetVo1.setState((byte)2);
        refundRetVo1.setDocumentType((byte)0);

        List<RefundRetVo> refundRetVoList=new ArrayList<>();
        refundRetVoList.add(refundRetVo);
        refundRetVoList.add(refundRetVo1);

        Mockito.when(paymentService.getRefund(0L,beginTime,endTime,1, 50)).thenReturn(new ReturnObject(refundRetVoList));
        Mockito.when(paymentService.getRefund(0L,beginTime,endTime,2, 50)).thenReturn(new ReturnObject(new ArrayList<RefundRetVo>()));

        Mockito.when(orderService.getOrderByOrderSn(Mockito.anyString())).thenReturn(new InternalReturnObject(orderFactory.create(666L)));

        Mockito.when(orderService.getOrderItemByOrderId(666L, 1,10)).thenReturn(new InternalReturnObject(ListFactory.create(orderItemFactory, 5)));
        Mockito.when(orderService.getOrderItemByOrderId(666L, 2,10)).thenReturn(new InternalReturnObject(ListFactory.create(orderItemFactory, 0)));

        Mockito.when(goodsService.getProduct(Mockito.anyLong())).thenReturn(new InternalReturnObject(productFactory.create(666L)));

        Mockito.when(shareService.getSuccessfulShareByOnSaleIdAdnCustomerId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject(successfulShareFactory.create(666L)));

        Mockito.when(customerService.modifyCustomerPoint(666L, new CustomerPointVo(2L))).thenReturn(new InternalReturnObject());

        Mockito.when(shareService.setSuccessfulShareLiquidated(Mockito.anyLong())).thenReturn(new InternalReturnObject());



        //售后退款
        AftersaleRetVo aftersaleRetVo=new AftersaleRetVo();
        aftersaleRetVo.setOrderItemId(3L);
        aftersaleRetVo.setQuantity(2);
        Mockito.when(aftersaleService.getAftersaleBySn("aftersalesn1")).thenReturn(new InternalReturnObject(aftersaleRetVo));

        //订单退款
        AftersaleRetVo aftersaleRetVo1=new AftersaleRetVo();
        aftersaleRetVo1.setOrderItemId(3L);
        aftersaleRetVo1.setQuantity(2);
        Mockito.when(aftersaleService.getAftersaleBySn("aftersalesn2")).thenReturn(new InternalReturnObject(aftersaleRetVo1));

        String responseString = this.mvc.perform(put("/shops/0/liquidation/start")
                .header("authorization", adminToken)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
