package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountVo;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 * @studentId 34520192201587
 */

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ShopAccountControllerTest {
    private static String adminToken;
    private static String shopToken;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    private static void login() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(2L, "shop-1", 1L, 2, 3600);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @Test
    public void addShopAccountTest() throws Exception {

        ShopAccountVo shopAccountVo= new ShopAccountVo();
        shopAccountVo.setAccount("1115");
        shopAccountVo.setType((byte) 1);
        shopAccountVo.setPriority((byte) 1);
        shopAccountVo.setName("test5");
        String requestJson= JacksonUtil.toJson(shopAccountVo);

        String expectResponse = "{\"errno\":0,\"data\":{\"type\":1,\"account\":\"1115\",\"name\":\"test5\",\"priority\":1,\"creator\":{\"id\":1,\"name\":\"wangxusheng\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        //测试新增记录需要移动优先级的情况
        String responseString=mvc.perform(post("/shops/2/accounts").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectResponse, responseString, false);

        expectResponse = "{\"errno\":0,\"data\":{\"type\":1,\"account\":\"1115\",\"name\":\"test5\",\"priority\":1,\"creator\":{\"id\":1,\"name\":\"wangxusheng\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        //测试新增记录不需要移动优先级的情况
        responseString=mvc.perform(post("/shops/5/accounts").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectResponse, responseString, false);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @Test
    public void getShopAccountsTest() throws Exception{

        String expectResponse="{\"errno\":0,\"data\":[{\"id\":2,\"type\":0,\"account\":\"111220333\",\"name\":\"甜蜜之旅支付宝帐号\",\"priority\":1,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":3,\"type\":1,\"account\":\"3112133333\",\"name\":\"甜蜜之旅微信帐号\",\"priority\":2,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}],\"errmsg\":\"成功\"}";

        String responseString=mvc.perform(get("/shops/2/accounts")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectResponse, responseString, false);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    @Test
    public void deleteShopAccountTest() throws Exception{
        //测试正确删除
        String response1=mvc.perform(delete("/shops/2/accounts/2")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}",response1);

        //测试accountId和shopId无法对应的情况
        String response2=mvc.perform(delete("/shops/2/accounts/5")
                .header("authorization", adminToken))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.RESOURCE_ID_NOTEXIST.getCode()))
                .andReturn().getResponse().getContentAsString();
        assertEquals("{\"errno\":504,\"errmsg\":\"账户信息有误！\"}",response2);
    }
}
