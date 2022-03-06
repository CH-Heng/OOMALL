package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.ActivityApplication;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.OnSaleCreatedVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Lin Jiyuan
 * @sn 30320192200032
 */
@SpringBootTest(classes = ActivityApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class GroupOnControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final InternalReturnObject getShopInfoRet1 = new InternalReturnObject(new SimpleShopVo(1L, "OOMALL自营商铺"));
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken;

    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.GoodsService")
    private GoodsService goodsService;
    @MockBean(name = "cn.edu.xmu.oomall.activity.microservice.ShopService")
    private ShopService shopService;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    private static final Locale LOCALE=Locale.CHINA;
    private DateTimeFormatter df=DateTimeFormatter.ofPattern(DATE_TIME_FORMAT,LOCALE);


    @Test  //测试下线成功
    public void offlineGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test  //测试下线失败,404错误
    public void notFoundGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1000/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test  //测试下线失败,当前状态禁止此操作
    public void failureOfflineGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        this.mvc.perform(put("/shops/0/groupons/2/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk());
        String responseString = this.mvc.perform(put("/shops/0/groupons/2/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test  //测试上线成功
    public void onlineGroupOnActivityTest() throws Exception {

        adminToken =jwtHelper.createToken(1L,"admin",0L, 2,40000);
        Mockito.when(goodsService.onlineOnSale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));

        Mockito.when(goodsService.offlineOnsale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));
        this.mvc.perform(put("/shops/0/groupons/2/offline").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String responseString = this.mvc.perform(put("/shops/0/groupons/2/online").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test  //测试上线失败,当前状态禁止此操作
    public void failureOnlineGroupOnActivityTest() throws Exception {

        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.onlineOnSale(Mockito.anyLong(),
                Mockito.anyLong())).thenReturn(new InternalReturnObject(ReturnNo.OK));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1/online").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除成功
    public void deleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        var response = this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andReturn().getResponse().getContentAsString();
        var list = response.split(",|:");
        var ids = list[4];
        Long id = Long.valueOf(ids);






        String responseString = this.mvc.perform(delete("/shops/0/groupons/"+id)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test   //删除失败，非草稿态
    public void failureDeleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/0/groupons/1")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test   //删除失败，404错误
    public void notFoundDeleteGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.deleteOnsale(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(delete("/shops/0/groupons/10000")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test//修改成功
    public void updateGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"13088admin",0L, 2,40000);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        OnsaleVo o1 = new OnsaleVo();
        o1.setId(Long.valueOf(1));
        OnsaleVo o2 = new OnsaleVo();
        o2.setId(Long.valueOf(2));
        OnsaleVo o3 = new OnsaleVo();
        o3.setId(Long.valueOf(3));
        List<OnsaleVo> list = new ArrayList<>();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        PageVo<OnsaleVo> po = new PageVo<>(1,5,5,5,list);
        Mockito.when(goodsService.getShopOnSaleInfo(Mockito.anyLong(),Mockito.anyLong(),Mockito.anyByte(),Mockito.any(),Mockito.any(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new InternalReturnObject(po));
        Mockito.when(goodsService.modifyOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(OnsaleModifyVo.class))).thenReturn(new InternalReturnObject(true));
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        var response = this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andReturn().getResponse().getContentAsString();
        var listResponse = response.split(",|:");
        var ids = listResponse[4];
        Long id = Long.valueOf(ids);


        String responseString = this.mvc.perform(put("/shops/0/groupons/"+id).contentType("application/json;charset=UTF-8").content(requestJson)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test//修改失败，非草稿态不可修改
    public void failureUpdateGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        OnsaleVo o1 = new OnsaleVo();
        o1.setId(Long.valueOf(1));
        OnsaleVo o2 = new OnsaleVo();
        o2.setId(Long.valueOf(2));
        OnsaleVo o3 = new OnsaleVo();
        o3.setId(Long.valueOf(3));
        List<OnsaleVo> list = new ArrayList<>();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        PageVo<OnsaleVo> po = new PageVo<>(1,5,5,5,list);
        Mockito.when(goodsService.getOnSales(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(),Mockito.any(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new InternalReturnObject(po));
        Mockito.when(goodsService.modifyOnsale(Mockito.anyLong(),Mockito.anyLong(),Mockito.any(OnsaleModifyVo.class))).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(put("/shops/0/groupons/1").contentType("application/json;charset=UTF-8").content(requestJson)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


    @Test//修改成功
    public void addOnSaleToGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"13088admin",0L, 2,40000);
        Mockito.when(goodsService.addOnSale(Mockito.anyLong(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject(true));

        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        var response = this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
        var list = response.split(",|:");
        var ids = list[4];
        Long id = Long.valueOf(ids);
        String responseString = this.mvc.perform(post("/shops/0/products/2/groupons/"+id+"/onsales").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content("{\"price\":\"100\",\"numKey\":\"1\",\"maxQuantity\":\"100\",\"quantity\":\"100\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    @Test//修改失败，当前状态禁止此操作
    public void failureAddOnSaleToGroupOnActivityTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(goodsService.addOnSale(Mockito.anyLong(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject(true));
        String responseString = this.mvc.perform(post("/shops/0/products/2/groupons/1/onsales").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content("{\"price\":\"100\",\"numKey\":\"1\",\"maxQuantity\":\"100\",\"quantity\":\"100\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }



}
