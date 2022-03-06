package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商品分类测试类
 *
 * @author Zhiliang Li
 * @date 2021/11/27
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private static String adminToken;
    private static String shopToken;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @BeforeAll
    private static void login() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(2L, "shop-1", 1L, 2, 3600);
    }

    @Test
    @Transactional
    public void addCategory() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        // 命名重复
        String requestJson = "{\n" +
                "  \"name\": \"女装男装\",\n" +
                "  \"commissionRatio\": 2\n" +
                "}";
        String responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":901,\"errmsg\":\"类目名称已存在\"}", responseString, false);

        // 找不到
        requestJson = "{\n" +
                "  \"name\": \"女装男装\",\n" +
                "  \"commissionRatio\": 2\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/500/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 成功插入，二级目录
        requestJson = "{\n" +
                "  \"name\": \"童装a\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/1/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"commissionRatio\":1,\"name\":\"童装a\"}}", responseString, false);

        // 成功插入，一级目录
        requestJson = "{\n" +
                "  \"name\": \"军械\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"commissionRatio\":1,\"name\":\"军械\"}}", responseString, false);

        // 不能插入成为三级目录
        requestJson = "{\n" +
                "  \"name\": \"手机游戏\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/277/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":967,\"errmsg\":\"不允许增加新的下级分类\"}", responseString, false);


        // 服务器错误
        requestJson = "{\n" +
                "  \"name\": \"conditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditionconditionitionconditioncondition\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/0/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":500}", responseString, false);

        // id小于0
        requestJson = "{\n" +
                "  \"name\": \"t1\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/0/categories/-1/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 非平台管理员访问
        requestJson = "{\n" +
                "  \"name\": \"童装\",\n" +
                "  \"commissionRatio\": 1\n" +
                "}";
        responseString = this.mvc.perform(post("/shops/1/categories/1/subcategories")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", shopToken)
                .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}", responseString, false);
    }

    @Test
    @Transactional
    public void getCategory() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String responseString;

        // 有子分类
        responseString = this.mvc.perform(get("/categories/1/subcategories"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 找不到
        responseString = this.mvc.perform(get("/categories/500/subcategories"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 无子分类
        responseString = this.mvc.perform(get("/categories/277/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 尝试查所有一级分类
        responseString = this.mvc.perform(get("/categories/0/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 查所有孤儿分类
        responseString = this.mvc.perform(get("/shops/0/orphancategories")
                .header("authorization", adminToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        responseString = this.mvc.perform(get("/shops/1/orphancategories")
                .header("authorization", shopToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}", responseString, false);
    }

    @Test
    @Transactional
    public void modifyCategory() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(redisUtil).del(Mockito.anyString());

        // 可以修改
        String requestJson = "{\"name\": \"test\"}";
        String responseString = this.mvc.perform(put("/shops/0/categories/313")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

//        // 重名
//        requestJson = "{\"name\": \"童装\"}";
//        responseString = this.mvc.perform(put("/shops/0/categories/313")
//                .contentType("application/json;charset=UTF-8")
//                .header("authorization", adminToken)
//                .content(requestJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        JSONAssert.assertEquals("{\"errno\":901,\"errmsg\":\"类目名称已存在\"}", responseString, false);

        // 找不到资源
        responseString = this.mvc.perform(put("/shops/0/categories/500")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 服务器错误
        requestJson = "{\"name\":\"conditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditionconditioncondition\"}";
        responseString = this.mvc.perform(put("/shops/0/categories/313")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":500}", responseString, false);

        // 非平台管理员访问
        requestJson = "{\"name\": \"test1\"}";
        responseString = this.mvc.perform(put("/shops/1/categories/313")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", shopToken)
                .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}", responseString, false);
    }

    @Test
    @Transactional
    public void deleteCategory() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(redisUtil).del(Mockito.anyString());

        // 删有子类别的
        String responseString = this.mvc.perform(delete("/shops/0/categories/1")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 删单独的
        responseString = this.mvc.perform(delete("/shops/0/categories/277")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 找不到
        responseString = this.mvc.perform(delete("/shops/0/categories/500")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 删为0或-1的
        responseString = this.mvc.perform(delete("/shops/0/categories/0")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 非平台管理员访问
        responseString = this.mvc.perform(delete("/shops/1/categories/2")
                .header("authorization", shopToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}", responseString, false);
    }

    @Test
    @Transactional
    public void getParentCategory() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(redisUtil).del(Mockito.anyString());

        // 获取成功
        String responseString = this.mvc.perform(get("/categories/273/parents"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, false);

        // 无该分类
        responseString = this.mvc.perform(get("/categories/500/parents"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);

        // 无父分类
        responseString = this.mvc.perform(get("/categories/1/parents"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", responseString, false);
    }

    @Test
    @Transactional
    public void getCategoryByIdTest() throws Exception {
        String responseString = this.mvc.perform(get("/internal/categories/270"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":270,\"name\":\"订餐服务\",\"commissionRatio\":1,\"pid\":15,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"gmtModified\":null,\"modifierName\":null}}";
        JSONAssert.assertEquals(expectResponse,responseString,false);
    }

    @Test
    @Transactional
    public void getCategoryDetailTest() throws Exception {
        String responseString = this.mvc.perform(get("/internal/categories/270/detail"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":270,\"name\":\"订餐服务\",\"commissionRatio\":1,\"pid\":15,\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":null,\"gmtModified\":null,\"modifierName\":null}}";
        JSONAssert.assertEquals(expectResponse,responseString,false);
    }
}
