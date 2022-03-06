package cn.edu.xmu.oomall.coupon;

import cn.edu.xmu.oomall.coupon.microservice.vo.ShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.coupon.model.bo.Shop;
import cn.edu.xmu.oomall.coupon.model.vo.CouponActivityVo;
import cn.edu.xmu.oomall.coupon.microservice.ShopFeignService;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

/**
 * @author RenJieZheng 22920192204334
 */
@Transactional      //防止脏数据
@SpringBootTest(classes = CouponApplication.class)
@AutoConfigureMockMvc      //自动初始化MockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouponActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "cn.edu.xmu.oomall.coupon.microservice.ShopFeignService")
    private ShopFeignService shopFeignService;

    private static String adminToken;

    @Autowired
    ResourceLoader resourceLoader;

    /**
     * 查看优惠活动模块的所有活动
     * @throws Exception 异常信息
     */
    @Test
    public void showAllState()throws Exception{
        //以下是正常情况返回的
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/couponactivities/states")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"data\":[\n" +
                "{\n" +
                "\"code\": 0,\n" +
                "\"name\": \"草稿\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": 1,\n" +
                "\"name\": \"上线\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": 2,\n" +
                "\"name\": \"下线\"\n" +
                "}\n" +
                "],\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    /**
     * 管理员新建己方优惠活动
     * @throws Exception 错误信息
     */
    @Test
    public void addCouponActivity()throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopVo shop = new ShopVo();
        shop.setId(1L);
        shop.setName("fasdfs");
        Mockito.when(shopFeignService.getShopById(1L)).thenReturn(new InternalReturnObject<>(shop));
        //以下是正常情况
        ZonedDateTime couponTime = ZonedDateTime.parse("2021-11-10T11:00:00.000+08:00");
        ZonedDateTime beginTime = ZonedDateTime.parse("2021-11-10T12:00:00.000+08:00");
        ZonedDateTime endTime = ZonedDateTime.parse("2021-11-10T17:00:00.000+08:00");
        CouponActivityVo couponActivityVo = new CouponActivityVo("双11大惠够", 100, (byte) 0, (byte) 0,couponTime,beginTime,endTime, "json",0);
        String json = JacksonUtil.toJson(couponActivityVo);
        String responseString;
        assert json != null;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/couponactivities")
                        .contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.err.println(responseString);
        String expectedString = "{\"errno\":0,\"data\":{\"name\":\"双11大惠够\",\"quantity\":100,\"imageUrl\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);


        //异常情况之领优惠券时间大于开始时间
        ZonedDateTime couponTime1 = ZonedDateTime.parse("2021-11-10T12:00:00.000+08:00");
        ZonedDateTime beginTime1 = ZonedDateTime.parse("2021-11-10T11:00:00.000+08:00");
        ZonedDateTime endTime1 = ZonedDateTime.parse("2021-11-10T17:00:00.000+08:00");
        CouponActivityVo couponActivityVo1 = new CouponActivityVo("双11大惠够", 100, (byte) 0, (byte) 0,couponTime1,beginTime1,endTime1, "json",0);
        String json1 = JacksonUtil.toJson(couponActivityVo1);
        String responseString1;
        assert json1 != null;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/couponactivities").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json1))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 950,\n" +
                "\"errmsg\": \"优惠卷领卷时间晚于活动开始时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        //异常情况之开始时间大于结束时间
        ZonedDateTime couponTime2 = ZonedDateTime.parse("2021-11-10T10:00:00.000+08:00");
        ZonedDateTime beginTime2 = ZonedDateTime.parse("2021-11-10T17:00:00.000+08:00");
        ZonedDateTime endTime2 = ZonedDateTime.parse("2021-11-10T13:00:00.000+08:00");
        CouponActivityVo couponActivityVo2 = new CouponActivityVo("双11大惠够", 100, (byte) 0, (byte) 0,couponTime2,beginTime2,endTime2, "json",0);
        String json2 = JacksonUtil.toJson(couponActivityVo2);
        String responseString2;
        assert json2 != null;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/couponactivities").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(json2))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 947,\n" +
                "\"errmsg\": \"开始时间不能晚于结束时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);


    }

    /**
     * 查看店铺所有状态的优惠活动列表
     * @throws Exception 异常信息
     */
    @Test
    public void showOwnInvalidCouponActivities() throws Exception {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        // 以下是正常情况应该返回的
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/3/couponactivities")
                        .param("state","1").param("page","1").param("pageSize","2")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    /**
     * 上传图片文件
     * @throws Exception 异常信息
     */
    @Test
    public void addCouponActivityImageUrl() throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        //正确运行
        String responseString0;
        Resource resource0 = new ClassPathResource("th.jpg");
        File file0 = resource0.getFile();
        InputStream inStream0 = new FileInputStream(file0);
        MockMultipartFile mfile0 = new MockMultipartFile("th.jpg", "th.jpg", ContentType.APPLICATION_OCTET_STREAM.toString(), inStream0);
        responseString0 = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/2/couponactivities/10/uploadImg")
                        .file(mfile0)
                        .header("authorization", adminToken)
                        .contentType("MediaType.MULTIPART_FORM_DATA_VALUE"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString0 = "{\n" +
                "\"errno\": 503,\n" +
                "\"errmsg\": \"字段不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString0,responseString0,false);

        //图片格式不正确
        String responseString1;
        Resource resource1 = new ClassPathResource("outSize.jpg");
        File file1 = resource1.getFile();
        InputStream inStream1 = getClass().getClassLoader().getResourceAsStream(file1.getCanonicalPath());
        MockMultipartFile mfile1 = new MockMultipartFile("file", "test.jpg", "jpg", inStream1);
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/2/couponactivities/10/uploadImg")
                        .file(mfile1)
                        .header("authorization", adminToken)
                        .contentType("MediaType.MULTIPART_FORM_DATA_VALUE"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);



        //资源不存在
        String responseString2;
        Resource resource2 = new ClassPathResource("outSize.jpg");
        File file2 = resource2.getFile();
        InputStream inStream2 = getClass().getClassLoader().getResourceAsStream(file2.getCanonicalPath());
        MockMultipartFile mfile2 = new MockMultipartFile("file", "test.jpg", "jpg", inStream2);
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/1/couponactivities/111/uploadImg")
                        .file(mfile2)
                        .header("authorization", adminToken)
                        .contentType("MediaType.MULTIPART_FORM_DATA_VALUE"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

        //操作的资源id不是自己的对象
        String responseString3;
        Resource resource3 = new ClassPathResource("outSize.jpg");
        File file3 = resource3.getFile();
        InputStream inStream3 = getClass().getClassLoader().getResourceAsStream(file3.getCanonicalPath());
        MockMultipartFile mfile3 = new MockMultipartFile("file", "test.jpg", "jpg", inStream3);
        responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/5/couponactivities/7/uploadImg")
                        .file(mfile3)
                        .header("authorization", adminToken)
                        .contentType("MediaType.MULTIPART_FORM_DATA_VALUE"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,false);

        //操作的资源id不是自己的对象
        String responseString4;
        Resource resource4 = new ClassPathResource("outSize.jpg");
        File file4 = resource4.getFile();
        InputStream inStream4 = getClass().getClassLoader().getResourceAsStream(file3.getCanonicalPath());
        MockMultipartFile mfile4 = new MockMultipartFile("file", "test.jpg", "jpg", inStream4);
        responseString4 = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/shops/1/couponactivities/7/uploadImg")
                        .file(mfile4)
                        .header("authorization", adminToken)
                        .contentType("MediaType.MULTIPART_FORM_DATA_VALUE"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString4 = "{\n" +
                "\"errno\": 507,\n" +
                "\"errmsg\": \"当前状态禁止此操作\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString4,responseString4,false);
    }

    /**
     * 查看所有的上线优惠活动列表
     * @throws Exception 异常信息
     */
    @Test
    public void showOwnCouponActivities() throws Exception{
        // 以下是正常情况应该返回的
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/couponactivities")
                        .param("shopId","1").param("beginTime","2021-11-01T00:23:58.235+08:00")
                        .param("endTime","2022-11-02T19:20:58.235+08:00").param("page","1").param("pageSize","2")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        // 开始时间晚于结束时间
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.get("/couponactivities")
                        .param("shopId","1").param("beginTime","2021-11-01T00:23:58.235+08:00")
                        .param("endTime","2021-10-02T19:20:58.235+08:00").param("page","1").param("pageSize","2")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 947,\n" +
                "\"errmsg\": \"开始时间不能晚于结束时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);
    }

    /**
     * 查看店铺的所有状态优惠活动列表
     * @throws Exception 异常信息
     */
    @Test
    public void showOwnCouponActivities1() throws Exception{
        // 以下是正常情况应该返回的
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shop/1/couponactivities")
                        .param("beginTime","2021-11-01T11:00:00.235+08:00")
                        .param("endTime","2022-11-10T19:00:00.235+08:00").param("page","1")
                        .param("pageSize","2").param("state","1")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        // 开始时间晚于结束时间
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shop/1/couponactivities")
                        .param("beginTime","2021-11-01T11:00:00.235+08:00")
                        .param("endTime","2021-01-10T19:00:00.235+08:00").param("page","1")
                        .param("pageSize","2").param("state","1")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 947,\n" +
                "\"errmsg\": \"开始时间不能晚于结束时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);
    }

    /**
     * 查看优惠活动详情
     * @throws Exception 异常信息
     */
    @Test
    public void showOwnCouponActivityInfo() throws Exception{
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        // 以下是正常情况应该返回的
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/2/couponactivities/5")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        // 以下是shopId和Id不匹配的情况
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/3/couponactivities/6")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

// 以下是shopId和Id不匹配的情况
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/3/couponactivities/100")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);

    }
     /**
     * 查看优惠活动详情
     * @throws Exception 异常信息
     */
    @Test
    public void showCouponActivityInfoTest() throws Exception {
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/internal/couponactivities/5")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString, responseString, false);
    }

}
