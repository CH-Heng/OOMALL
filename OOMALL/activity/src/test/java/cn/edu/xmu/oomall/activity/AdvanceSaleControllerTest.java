//package cn.edu.xmu.oomall.activity;
//
//import cn.edu.xmu.oomall.activity.microservice.GoodsService;
//import cn.edu.xmu.oomall.activity.model.vo.*;
//import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
//import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.mockito.Mockito;
//import org.skyscreamer.jsonassert.JSONAssert;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.nio.charset.Charset;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
////事务回滚(测试的时候不会产生脏数据)
//@Rollback
//@SpringBootTest(classes = ActivityApplication.class)
//@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
//public class AdvanceSaleControllerTest {
//    private static String adminToken = "0";
//    private static String shopToken = "0";
//    private MockMvc mvc;
//
//    @MockBean
//    private GoodsService goodsService;
//    @Autowired
//    private WebApplicationContext wac;
//    @MockBean
//    private RedisUtil redisUtil;
//    final Charset charset=Charset.forName("UTF-8");
//
//    @BeforeEach
//    public void init() {
//        mvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象
//        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
//        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
//    }
//    @Test
//    public void onlineAdvancesale1() throws Exception {            //上架成功（重新上架
//        Mockito.when(goodsService.onlineOnsale(4L,1L)).thenReturn(new InternalReturnObject());
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/4/advancesales/1/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void onlineAdvancesale2() throws Exception {            //上架成功(首次上架
//        Mockito.when(goodsService.onlineOnsale(5L,3L)).thenReturn(new InternalReturnObject());
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/3/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void onlineAdvancesale3() throws Exception {        //不是同一个商家
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/1/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).
//                andDo(print()).
//                andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"管理员和预售活动不属于同一个商铺，无权限\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void onlineAdvancesale4() throws Exception {    //活动不存在
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/4/advancesales/1000/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).
//                andDo(print()).
//                andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":504,\"errmsg\":\"目标预售活动不存在\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void onlineAdvancesale5() throws Exception {        //活动已上架
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/2/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).
//                andDo(print()).
//                andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void onlineAdvancesale6() throws Exception {            //上架失败，内部API调用不成功
//        Mockito.when(goodsService.onlineOnsale(5L,3L)).thenReturn(new InternalReturnObject(500,"数据库内部错误"));
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/3/online").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is5xxServerError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":500,\"errmsg\":\"数据库内部错误\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //---------------------------------------------------下架预售活动----------------------------------------------------------------
//    @Test
//    public void offlineAdvancesale1() throws Exception {            //下架失败（已下架
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/2/advancesales/4/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).
//                andDo(print()).
//                andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void offlineAdvancesale2() throws Exception {            //下架失败（还未上架
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/3/advancesales/6/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void offlineAdvancesale3() throws Exception {        //不是同一个商家
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/3/advancesales/5/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"管理员和预售活动不属于同一个商铺，无权限\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void offlineAdvancesale4() throws Exception {    //活动不存在
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/2/advancesales/1000/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":504,\"errmsg\":\"目标预售活动不存在\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void offlineAdvancesale5() throws Exception {        //下架成功
//        Mockito.when(goodsService.offlineOnsale(2L,5L)).thenReturn(new InternalReturnObject(0,"成功"));
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/2/advancesales/5/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    @Test
//    public void offlineAdvancesale6() throws Exception {        //下架成功
//        Mockito.when(goodsService.offlineOnsale(2L,5L)).thenReturn(new InternalReturnObject(500,"数据库内部错误"));
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/2/advancesales/5/offline").header("authorization", shopToken).contentType("application/json;charset=UTF-8")).
//                andExpect(MockMvcResultMatchers.status().is5xxServerError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":500,\"errmsg\":\"数据库内部错误\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //--------------------------------------------修改预售活动------------------------------------------------------------------
//    //店铺不同
//    @Test
//    public void modifyAdvancesale1() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/4/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"管理员和预售活动不属于同一个商铺，无权限\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //状态不允许
//    @Test
//    public void modifyAdvancesale2() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/4/advancesales/1").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //状态不允许
//    @Test
//    public void modifyAdvancesale3() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/2").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //活动不存在
//    @Test
//    public void modifyAdvancesale4() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/1000").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":504,\"errmsg\":\"该预售活动不存在\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //修改成功
//    @Test
//    public void modifyAdvancesale5() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        OnsaleVo onsaleVo=new OnsaleVo(1L,2L,LocalDateTime.now(),LocalDateTime.now(),5);
//        List<OnsaleVo> list=new ArrayList<OnsaleVo>();
//        list.add(onsaleVo);
//        PageVo<OnsaleVo> pageVo=new PageVo<OnsaleVo>(1,10,1,4,list);
//        InternalReturnObject<PageVo<OnsaleVo>> pageRetObj=new InternalReturnObject<PageVo<OnsaleVo>>(pageVo);
//        Mockito.when(goodsService.getShopOnSaleInfo(5L,3L,(byte)1,null,null,1,10)).thenReturn(pageRetObj);
//        OnsaleModifyVo onsaleModifyVo=new OnsaleModifyVo();
//        Mockito.when(goodsService.modifyOnsale(5L,1L,onsaleModifyVo)).thenReturn(new InternalReturnObject());
//        AdvanceSaleModifyVo vo=new AdvanceSaleModifyVo();
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //modifyOnsale失败导致失败
//    @Test
//    public void modifyAdvancesale6() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        OnsaleVo onsaleVo=new OnsaleVo(1L,2L,LocalDateTime.now(),LocalDateTime.now(),5);
//        List<OnsaleVo> list=new ArrayList<OnsaleVo>();
//        list.add(onsaleVo);
//        PageVo<OnsaleVo> pageVo=new PageVo<OnsaleVo>(1,10,1,4,list);
//        InternalReturnObject<PageVo<OnsaleVo>> pageRetObj=new InternalReturnObject<PageVo<OnsaleVo>>(pageVo);
//        Mockito.when(goodsService.getShopOnSaleInfo(5L,3L,(byte)1,null,null,1,10)).thenReturn(pageRetObj);
//        OnsaleModifyVo onsaleModifyVo=new OnsaleModifyVo();
//        Mockito.when(goodsService.modifyOnsale(5L,1L,onsaleModifyVo)).thenReturn(new InternalReturnObject(500,"出错"));
//        AdvanceSaleModifyVo vo=new AdvanceSaleModifyVo();
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/5/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is5xxServerError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":500,\"errmsg\":\"出错\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //----------------------------删除预售-----------------------------------------------------------------
//    //店铺不同
//    @Test
//    public void deleteAdvancesale1() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/4/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"管理员和预售活动不属于同一个商铺，无权限\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //状态不允许
//    @Test
//    public void deleteAdvancesale2() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/4/advancesales/1").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //状态不允许
//    @Test
//    public void deleteAdvancesale3() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/5/advancesales/2").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //活动不存在
//    @Test
//    public void deleteAdvancesale4() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/5/advancesales/1000").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is4xxClientError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":504,\"errmsg\":\"该预售活动不存在\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //删除成功
//    @Test
//    public void deleteAdvancesale5() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        Mockito.when(goodsService.deleteOnsale(5L,3L)).thenReturn(new InternalReturnObject());
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/5/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//    //删除失败，onsale删除异常
//    @Test
//    public void deleteAdvancesale6() throws Exception {
//        String json="{\n" +
//                "   \"advancePayPrice\":15,\n" +
//                "    \"name\":\"商铺管理员\"\n" +
//                "}";
//        Mockito.when(goodsService.deleteOnsale(5L,3L)).thenReturn(new InternalReturnObject(500,"数据库内部错误"));
//        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/shops/5/advancesales/3").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(json)).
//                andExpect(MockMvcResultMatchers.status().is5xxServerError()).andDo(print()).andReturn().getResponse().getContentAsString(charset);
//        System.out.println(responseString);
//        String expectedResponse="{\"errno\":500,\"errmsg\":\"数据库内部错误\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//    }
//}
