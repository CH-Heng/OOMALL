package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.FullOnSaleVo;
import cn.edu.xmu.oomall.activity.microservice.vo.ModifyOnSaleVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.activity.model.bo.OnSale;
import cn.edu.xmu.oomall.activity.model.vo.PageVo;
import cn.edu.xmu.oomall.activity.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.activity.model.vo.StrategyVo;
import cn.edu.xmu.oomall.activity.util.CreateObject;
import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ShareActivityController Tester.
 *
 * @author xiuchen lang 22920192204222
 * @date 11/13/2021
 */
@SpringBootTest(classes = ActivityApplication.class)
@AutoConfigureMockMvc
@Rollback(value = true)
@Transactional
public class ShareActivityControllerTest {

    private static String token = "0";
    private static String adminToken;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private RedisUtil redisUtil;

    JwtHelper jwtHelper = new JwtHelper();

    @BeforeEach
    public void init() throws Exception {
        //生成一个 onsale对象
        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> onsaleInfoDTO = CreateObject.createOnSaleInfoDTO(1L);
        InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> onsaleInfoDTO1 = CreateObject.createOnSaleInfoDTO(-1L);

        Mockito.when(goodsService.getOnSales(2L, 1L, null, null, 1, 10)).thenReturn(onsaleInfoDTO);
        Mockito.when(goodsService.getOnSales(null, 1L, null, null, 1, 10)).thenReturn(onsaleInfoDTO);
        Mockito.when(goodsService.getOnSales(1L, 1L, null, null, 1, 10)).thenReturn(onsaleInfoDTO);
        Mockito.when(goodsService.getOnSales(2L, -1L, null, null, 1, 10)).thenReturn(onsaleInfoDTO1);
        Mockito.when(goodsService.getOnSales(11111L, 1L, null, null, 1, 10)).thenReturn(onsaleInfoDTO1);
        //生成一个shop对象
        InternalReturnObject<SimpleShopVo> shopInfoDTO = CreateObject.createShopInfoDTO(1L);
        InternalReturnObject<SimpleShopVo> shopInfoDTO2 = CreateObject.createShopInfoDTO(2L);
        InternalReturnObject<SimpleShopVo> shopInfoDTO1 = CreateObject.createShopInfoDTO(-1L);
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(shopInfoDTO);
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(shopInfoDTO2);
        Mockito.when(shopService.getShopInfo(11L)).thenReturn(shopInfoDTO1);
        Mockito.when(shopService.getShopInfo(-1L)).thenReturn(shopInfoDTO1);

        //redis配置
        Mockito.when(redisUtil.get("shareactivivybyid_10")).thenReturn(null);
        Mockito.when(redisUtil.get("shareactivivybyid_1")).thenReturn("{\"@class\":\"cn.edu.xmu.oomall.activity.model.bo.ShareActivityBo\",\"id\":1,\"shopId\":2,\"shopName\":\"甜蜜之旅\",\"name\":\"分享活动1\",\"beginTime\":\"2021-11-11 15:01:23.000\",\"endTime\":\"2022-02-19 15:01:23.000\",\"state\":1,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11 15:01:23.000\",\"gmtModified\":null,\"strategy\":null}");
        Mockito.when(redisUtil.get("shareactivivyid_1_shopid_10")).thenReturn(null);
        Mockito.when(redisUtil.get("shareactivivyid_1_shopid_2")).thenReturn("{\"@class\":\"cn.edu.xmu.oomall.activity.model.bo.ShareActivityBo\",\"id\":1,\"shopId\":2,\"shopName\":\"甜蜜之旅\",\"name\":\"分享活动1\",\"beginTime\":\"2021-11-11 15:01:23.000\",\"endTime\":\"2022-02-19 15:01:23.000\",\"state\":1,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"modifierName\":null,\"gmtCreate\":\"2021-11-11 15:01:23.000\",\"gmtModified\":null,\"strategy\":null}");
        token = jwtHelper.createToken(666L, "lxc", 0L, 1, 5000);
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 5000);
    }

    /**
     * 获得分享活动的所有状态
     * Method: getShareState()
     */
    @Test
    public void testGetShareState() throws Exception {
        String responseString = mvc.perform(get("/shareactivities/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    public void testGetShareActById() throws Exception {
        String responseString = mvc.perform(get("/internal/shareactivities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":404,\"errmsg\":\"资源不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    /**
     * 显示所有状态的分享活动
     * Method: getShareByShopId(@PathVariable(name = "shopId", required = true) Long shopId, @RequestParam(name = "productId", required = false) Long productId, @RequestParam(name = "beginTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime, @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime, @RequestParam(name = "state", required = false) Byte state, @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "pageSize", required = false) Integer pageSize)
     */
    @Test
    public void testGetShareByShopId() throws Exception {
        //不添加query时
        String responseString = mvc.perform(get("/shops/2/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"分享活动1\"},{\"id\":7,\"name\":\"分享活动7\"},{\"id\":10,\"name\":\"分享活动10\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);


        String responseString4 = mvc.perform(get("/shops/-1/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString4 = "{\"errno\":503,\"errmsg\":\"shopId错误\"}";
        JSONAssert.assertEquals(expectString4, responseString4, true);

        String responseString5 = mvc.perform(get("/shops/2/shareactivities?productId=-1").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString5 = "{\"errno\":503,\"errmsg\":\"productId错误\"}";
        JSONAssert.assertEquals(expectString5, responseString5, true);

/*        //有添加所有query都有时且合规
        String responseString6 = mvc.perform(get("/shops/2/shareactivities?productId=1&state=1&page=1&pageSize=3").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString6 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString6, responseString6, false);*/


/*        String responseString7 = mvc.perform(get("/shops/1111111/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString7 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]}}";
        JSONAssert.assertEquals(expectString7, responseString7, true);*/
    }

    /**
     * 管理员新增分享活动
     * Method: addShareAct(@PathVariable(value = "shopId", required = true) Long shopId, @Validated @RequestBody ShareActivityVo shareactivityDTO, BindingResult bindingResult)
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testAddShareAct() throws Exception {
        CustomComparator CUSTOM_COMPARATOR = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("data.id", (o1, o2) -> true));
        String requestJson = "{\"name\":\"String\",\"beginTime\":\"2021-11-11T15:01:02.000+08:00\",\"endTime\":\"2021-11-11T15:01:10.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":10},{\"quantity\":10,\"percentage\":10}]}";
//        //有添加所有query都有时且合规
        String responseString = mvc.perform(post("/shops/2/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, CUSTOM_COMPARATOR);

//        //所有都合规
        String requestJson7 = "{\"name\":\"我是一个活动\",\"beginTime\":\"2021-11-11T15:01:02.000+08:00\",\"endTime\":\"2021-11-11T15:01:10.000+08:00\",\"strategy\":[{\"quantity\":5,\"percentage\":10},{\"quantity\":10,\"percentage\":10}]}";
        String responseString7 = mvc.perform(post("/shops/2/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8").content(requestJson7))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString7 = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectString7, responseString7, false);

    }

    /**
     * 查询分享活动 只显示上线状态的分享活动
     * Method: getShareActivity(@RequestParam(name = "shopId", required = false) Long shopId, @RequestParam(name = "productId", required = false) Long productId, @RequestParam(name = "beginTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime, @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime, @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "pageSize", required = false) Integer pageSize)
     */
    @Test
    public void testGetShareActivity() throws Exception {
        //不添加query时
        String responseString = mvc.perform(get("/shareactivities").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);

/*        //有shopId
        String responseString1 = mvc.perform(get("/shareactivities?shopId=1").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, true);

        //有productId
        String responseString2 = mvc.perform(get("/shareactivities?productId=1").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString2 = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"分享活动2\"},{\"id\":3,\"name\":\"分享活动3\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString2, responseString2, true);

        //都合规
        String responseString6 = mvc.perform(get("/shareactivities?shopId=1&productId=1&beginTime=2021-11-11 15:01:02.000&endTime=2021-11-11 15:01:02.000&page=1&pageSize=4").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString6 = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":4,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString6, responseString6, true);

        //shopId<0
        String responseString7 = mvc.perform(get("/shareactivities?shopId=-1&productId=1&beginTime=2021-11-11 15:01:02.000&endTime=2021-11-11 15:01:02.000&page=1&pageSize=4").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString7 = "{\"errno\":503,\"errmsg\":\"shopId错误\"}";
        JSONAssert.assertEquals(expectString7, responseString7, true);


        //productId<0
        String responseString8 = mvc.perform(get("/shareactivities?productId=-1").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString8 = "{\"errno\":503,\"errmsg\":\"productId错误\"}";
        JSONAssert.assertEquals(expectString8, responseString8, true);*/
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     * Method: getShareActivityById(@PathVariable(value = "id", required = true) Long id)
     */
    @Test
    public void testGetShareActivityById() throws Exception {
//        String responseString = mvc.perform(get("/shareactivities/1").header("authorization", token).contentType("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectString = "{\"errno\":0,\"data\":{\"id\":1,\"shop\":{\"id\":1,\"name\":\"甜蜜之旅\"},\"name\":\"分享活动1\",\"beginTime\":\"2021-11-11 15:01:23.000\",\"endTime\":\"2022-02-19 15:01:23.000\",\"strategy\":null},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectString, responseString, true);

        //没redis
        String responseString1 = mvc.perform(get("/shareactivities/10").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, false);
/*
        String responseString2 = mvc.perform(get("/shareactivities/2").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString2 = "{\"errno\":0,\"data\":{\"id\":2,\"shop\":{\"id\":3,\"name\":\"向往时刻\"},\"name\":\"分享活动2\",\"beginTime\":\"2021-11-11 15:01:23.000\",\"endTime\":\"2022-02-19 15:01:23.000\",\"strategy\":[{\"quantity\":10,\"percentage\":10},{\"quantity\":10,\"percentage\":10}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString2,responseString2, true);


        String responseString3 = mvc.perform(get("/shareactivities/111111").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString3 = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString3, responseString3, true);*/
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     * Method: getShareActivityByShopIdAndId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id)
     */
    @Test
    public void testGetShareActivityByShopIdAndId() throws Exception {
//        String responseString = mvc.perform(get("/shops/2/shareactivities/1").header("authorization", token).contentType("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectString = "{\"errno\":0,\"data\":{\"id\":1,\"shop\":{\"id\":1,\"name\":\"甜蜜之旅\"},\"name\":\"分享活动1\",\"beginTime\":\"2021-11-11 15:01:23.000\",\"endTime\":\"2022-02-19 15:01:23.000\",\"state\":1,\"createdBy\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-11 15:01:23.000\",\"gmtModified\":null,\"modifiedBy\":{\"id\":null,\"name\":null},\"strategy\":null},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectString, responseString, true);

        //没redis
        String responseString1 = mvc.perform(get("/shops/2/shareactivities/10").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, false);

/*        String responseString2 = mvc.perform(get("/shops/3/shareactivities/2").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString2 = " {\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString2,responseString2, true);

        String responseString3 = mvc.perform(get("/shops/2/shareactivities/11111").header("authorization", token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString3 = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectString3, responseString3, true);*/
    }

    //--------------------------------------------------------//
    /**
     * @author BingShuai Liu 22920192204245
     * @throws Exception
     */
    @Test
    @Transactional
    public void addShareActivityOnOnSale_OnSaleOfflineState() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 2);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject(ReturnNo.STATENOTALLOW.getCode(),ReturnNo.STATENOTALLOW.getMessage()));
        String responseString=this.mvc.perform(post("/shops/1/onsales/1/shareactivities/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
/*    @Test
    @Transactional
    public void addShareActivityOnOnSale_ShareActivityOfflineState() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,2L,modifyOnSaleVo)).thenReturn(new InternalReturnObject<>());
        String responseString=this.mvc.perform(post("/shops/1/onsales/1/shareactivities/2").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }*/
/*   @Test
    @Transactional
    public void addShareActivityOnOnSale_ShareActivityIdNotFound() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,-1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject<>());
        String responseString=this.mvc.perform(post("/shops/1/onsales/1/shareactivities/-1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }*/
/*    @Test
    @Transactional
    public void addShareActivityOnOnSale_ShareActivityOnlineState() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject<>());
        String responseString=this.mvc.perform(post("/shops/1/onsales/1/shareactivities/1").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }*/
/*    @Test
    @Transactional
    public void addShareActivityOnOnSale_ShareActivityDraftState() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(4L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject());
        String responseString=this.mvc.perform(post("/shops/1/onsales/1/shareactivities/4").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }*/
    @Test
    @Transactional
    public void deleteShareActivityOnOnSale_ShareActivityIdNotFound() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,-1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject());
        String responseString=this.mvc.perform(delete("/shops/1/onSales/1/shareActivities/1").header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
/*        String expectedString= "{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);*/
    }
    @Test
    @Transactional
    public void deleteShareActivityOnOnSale_Success() throws Exception{
        FullOnSaleVo onsale = new FullOnSaleVo();
        onsale.setId(1L);
        onsale.setState((byte) 1);
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(-1L);
        Mockito.when(goodsService.getOnSaleById(1L)).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsService.modifyOnSaleShareActId(1L,1L,modifyOnSaleVo)).thenReturn(new InternalReturnObject<>());
        String responseString=this.mvc.perform(delete("/shops/1/onSale/1/shareActivities/4").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void modifyShareActivity_NotDraftState() throws Exception{
        StrategyVo shareactivityStrategyVo=new StrategyVo(10,10);
        List<StrategyVo> list = new ArrayList<>();
        list.add(shareactivityStrategyVo);
        ShareActivityVo shareactivityVo = new ShareActivityVo();
        shareactivityVo.setName("分享活动5");
        shareactivityVo.setStrategy(list);
        String json= JacksonUtil.toJson(shareactivityVo);
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString="{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void modifyShareActivity_BeginTimeAfterEndTime() throws Exception{
        StrategyVo shareactivityStrategyVo=new StrategyVo(10,10);
        List<StrategyVo> list = new ArrayList<>();
        list.add(shareactivityStrategyVo);
        ShareActivityVo shareactivityVo = new ShareActivityVo();
        shareactivityVo.setName("测试活动");
        shareactivityVo.setStrategy(list);
        String json= JacksonUtil.toJson(shareactivityVo);
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/4")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString=" {\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void modifyShareActivity_ShareActivityIdNotFound() throws Exception{
        StrategyVo shareactivityStrategyVo=new StrategyVo(10,10);
        List<StrategyVo> list = new ArrayList<>();
        list.add(shareactivityStrategyVo);
        ShareActivityVo shareactivityVo = new ShareActivityVo();
        shareactivityVo.setName("测试活动");
        shareactivityVo.setStrategy(list);
        String json= JacksonUtil.toJson(shareactivityVo);
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void modifyShareActivity_Success() throws Exception{
        StrategyVo shareactivityStrategyVo=new StrategyVo(10,10);
        List<StrategyVo> list = new ArrayList<>();
        list.add(shareactivityStrategyVo);
        ShareActivityVo shareactivityVo = new ShareActivityVo();
        shareactivityVo.setName("测试活动");
        shareactivityVo.setStrategy(list);
        String json= JacksonUtil.toJson(shareactivityVo);
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/4")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void deleteShareActivity_ShareActivityIdNotFound() throws Exception{
        String responseString=this.mvc.perform(delete("/shops/1/shareactivities/-1")
                        .header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString="{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void deleteShareActivity_NotDraftState() throws Exception{
        String responseString=this.mvc.perform(delete("/shops/1/shareactivities/1")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void deleteShareActivity_Success() throws Exception{
        String responseString=this.mvc.perform(delete("/shops/1/shareactivities/4")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOnline_NotOfflineState() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/1/online")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOnline_ShareActivityIdNotFound() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/-1/online")
                        .header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOnline_Success() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/2/online")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOffline_NotOnlineState() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/3/offline")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 507,\n" +
                "\t\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOffline_ShareActivityIdNotFound() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/-1/offline")
                        .header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 504,\n" +
                "\t\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
    @Test
    @Transactional
    public void shareactivityOffline_Success() throws Exception{
        String responseString=this.mvc.perform(put("/shops/1/shareactivities/1/offline")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\t\"errno\": 0,\n" +
                "\t\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }
}
