package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.*;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.oomall.activity.model.vo.SimpleUserRetVo;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jiawei Zheng
 * @date 2021-11-27
 */

@SpringBootTest(classes = ActivityApplication.class)
@AutoConfigureMockMvc
@Rollback(value = true)
public class AdvanceSaleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.ShopService")
    private ShopService shopService;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.GoodsService")
    private GoodsService goodsService;

    private static final String DATE_TIME_FORMAT = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final Locale LOCALE=Locale.CHINA;

    private DateTimeFormatter df;
    private static String adminToken;
    JwtHelper jwtHelper = new JwtHelper();

    PageVo<SimpleOnSaleInfoVo> page1 = new PageVo<SimpleOnSaleInfoVo>();
    List<SimpleOnSaleInfoVo>list1=new ArrayList<>();

    FullOnSaleVo vo3=new FullOnSaleVo(3L,new SimpleShopVo(4L,"努力向前"),new ProductVo(1L,null,null),
            null,null, null, null,(byte)3,1L,1L,new SimpleUserRetVo(1L,null),
            null,null, new SimpleUserRetVo(1L,null),(byte)1);

    @BeforeEach
    public void init() {
        df = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, LOCALE);
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

        SimpleOnSaleInfoVo vo1=new SimpleOnSaleInfoVo();
        vo1.setId(18L);
        vo1.setActivityId(3L);
        list1.add(vo1);
        page1.setList(list1);
    }

    @Test
    @Transactional
    public void getAdvanceSaleStatesTest() throws Exception {
        String responseString = mvc.perform(get("/advancesales/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"草稿\"},{\"code\":1,\"name\":\"上线\"},{\"code\":2,\"name\":\"下线\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //1.不添加任何查询条件
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest1() throws Exception {
        String responseString = this.mvc.perform(get("/advancesales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":6,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"预售活动2\"},{\"id\":5,\"name\":\"预售活动5\"},{\"id\":7,\"name\":\"预售活动7\"},{\"id\":8,\"name\":\"预售活动8\"},{\"id\":9,\"name\":\"预售活动9\"},{\"id\":10,\"name\":\"预售活动10\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    // 3.根据shopId查询(shopId存在)
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest3() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
        String responseString = mvc.perform(get("/advancesales?shopId=1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":8,\"name\":\"预售活动8\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    // 4.根据shopId查询(shopId不存在)
    @Test
    @Transactional
    public void getAllOnlineAdvanceSaleTest4() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(),"找不到该商铺"));
        String responseString = mvc.perform(get("/advancesales?shopId=1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 查询上线预售活动的详细信息
     */
    //1.查询activityid为2的预售活动的详细信息，成功查到
    @Test
    @Transactional
    public void getOnlineAdvanceSaleInfoTest1() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(shopService.getShopInfo(5L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(5L,"坚持就是胜利")));
        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> pageInfoReturnObject=new InternalReturnObject<>(page1);
        Mockito.when(goodsService.getShopOnSaleInfo(5L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject.getData().getList().get(0).getId())).thenReturn(new InternalReturnObject<>(vo3));
        String responseString = mvc.perform(get("/advancesales/2"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected ="{\"errno\":0,\"data\":{\"id\":2,\"name\":\"预售活动2\",\"shop\":{\"id\":4,\"name\":\"努力向前\"}},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    //1.根据shopId,productId,beginTime,endTime查询
    @Test
    @Transactional
    public void getShopAdvanceSaleTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",5L, 1,3600);
        Mockito.when(shopService.getShopInfo(5L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(5L,"坚持就是胜利")));
        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(page1);
        Mockito.when(goodsService.getOnSales(5L,1552L,null,null,1,1)).thenReturn(listReturnObject);
        String responseString = mvc.perform(get("/shops/5/advancesales?productId&beginTime&endTime")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"预售活动2\"},{\"id\":3,\"name\":\"预售活动3\"}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    // 2.根据shopId查询(shopId不存在)
    @Test
    @Transactional
    public void getShopAdvanceSaleTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L, 1,3600);
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(),"找不到该商铺"));
        String responseString = mvc.perform(get("/shops/1/advancesales")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect((status().isNotFound()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

//    /**
//     * 管理员新增预售
//     */
//    @Test
//    @Transactional
//    public void addAdvanceSaleTest1() throws Exception {
//        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
//        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"努力向前")));
//        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> listReturnObject = new InternalReturnObject<>(page1);
//        Mockito.when(goodsService.getOnSales(4L,1552L,null,null,1,1)).thenReturn(listReturnObject);
//        String requestJson="{\"price\": 156,\"beginTime\": \"2029-06-21T17:38:20.000+08:00\",\"endTime\": \"2029-12-29T17:38:20.000+08:00\",\"quantity\": 2,\"name\": \"预售活动11\",\"payTime\": \"2029-06-22T17:38:20.000+08:00\",\"advancePayPrice\": 140}";
//        String responseString = mvc.perform(post("/shops/4/products/1552/advancesales")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8").content(requestJson))
//                .andExpect((status().isOk()))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "";
//        JSONAssert.assertEquals(expected, responseString, false);
//    }
//
//    /**
//     * 管理员查询商铺的特定预售活动
//     * @throws Exception
//     */
//    @Test
//    public void getShopAdvanceSaleInfoTest1() throws Exception {
//        adminToken =jwtHelper.createToken(1L,"13088admin",0L, 2,3600);
//        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
//        Mockito.when(shopService.getShopInfo(5L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(5L,"坚持就是胜利")));
//        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> pageInfoReturnObject=new InternalReturnObject<>(page1);
//        Mockito.when(goodsService.getShopOnSaleInfo(5L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject);
//        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject.getData().getList().get(0).getId())).thenReturn(new InternalReturnObject<>(vo3));
//        String responseString = mvc.perform(get("/shops/5/advancesales/2")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isOk()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "";
//        JSONAssert.assertEquals(expected, responseString, false);
//    }

//    //2.查询shopId为11,activityid为1的预售活动，shopId为空
//    @Test
//    public void getShopAdvanceSaleInfoTest2() throws Exception {
//        adminToken =jwtHelper.createToken(1L,"admin",11L, 1,3600);
//        Mockito.when(shopService.getShopInfo(11L)).thenReturn(new InternalReturnObject<>());
//        String responseString = mvc.perform(get("/shops/11/advancesales/1")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isNotFound()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }
//
//    //3.查询shopId为4,activityid为2的预售活动，活动id和店铺不匹配，getShopOnsaleInfo查不到
//    @Test
//    public void getShopAdvanceSaleInfoTest3() throws Exception {
//        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
//        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
//        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject1=new InternalReturnObject<>(new PageInfo<>());
//        Mockito.when(goodsService.getShopOnSaleInfo(4L,2L,null,null,null,1,10)).thenReturn(pageInfoReturnObject1);
//        String responseString = mvc.perform(get("/shops/4/advancesales/2")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isNotFound()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected="{\"errno\":504,\"errmsg\":\"找不到该预售活动对应的销售信息\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }
//
//    //4.查询shopId为4,activityid为11的预售活动，假设OnSale找得到，AdvanceSale找不到，会在dao层出错
//    @Test
//    public void getShopAdvanceSaleInfoTest4() throws Exception {
//        adminToken =jwtHelper.createToken(1L,"admin",4L, 1,3600);
//        Mockito.when(shopService.getShopInfo(4L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"OOMALL自营商铺")));
//        InternalReturnObject<PageInfo<SimpleOnSaleInfoVo>> pageInfoReturnObject1=new InternalReturnObject<>(new PageInfo<>(list2));
//        Mockito.when(goodsService.getShopOnSaleInfo(4L,11L,null,null,null,1,10)).thenReturn(pageInfoReturnObject1);
//        InternalReturnObject<FullOnSaleVo> returnObject=new InternalReturnObject<>(list3.get(0));
//        Mockito.when(goodsService.getOnSaleById(pageInfoReturnObject1.getData().getList().get(0).getId())).thenReturn(returnObject);
//        String responseString = mvc.perform(get("/shops/4/advancesales/11")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8"))
//                .andExpect((status().isNotFound()))
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expected="{\"errno\":504,\"errmsg\":\"活动不存在\"}";
//        JSONAssert.assertEquals(expected, responseString, true);
//    }
}
