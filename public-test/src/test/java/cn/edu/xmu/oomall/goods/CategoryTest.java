package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.goods.vo.CategoryRetVo;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * description: BrandCategoryTest
 * date: 2020/12/12 22:54
 * author1: 张悦 10120182203143
 * author2: 岳皓 24320182203319
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTest extends BaseTestOomall {

    private static final String SUBCATEGORY = "/shop/categories/{id}/subcategories";
    private static final String PARENTCATEGORY = "/shop/categories/{id}/parents";
    private static final String SHOPCATEGORY = "/shop/shops/{shopId}/categories/{id}/subcategories";
    private static final String CATEGORY = "/shop/shops/{shopId}/categories/{id}";
    private static final String ORPHAN = "/shop/shops/{shopId}/orphancategories";

    private static Long categoryId1 = null;
    private static Long categoryId2 = null;
    /** 1
     * 不需要登录-查询商品分类关系1-存在该分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    public void getCategorySubsTest1() throws Exception {
        this.mallClient.get().uri(SUBCATEGORY, 0)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.id == '1')].name").isEqualTo("女装男装")
                .jsonPath("$.data[?(@.id == '2')].name").isEqualTo("鞋类箱包");
    }
    /** 2
     * 不需要登录-查询商品分类关系2-不存在该分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    public void getCategorySubs2() throws Exception {//检测如果没有此id
        mallClient.get().uri(SUBCATEGORY,100000)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /** 3
     * 不需要登录-查询商品分类关系3-该分类下无子分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    public void getCategorySubs3() throws Exception {
        mallClient.get().uri(SUBCATEGORY,313)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(0);
    }

    /** 17
     * 需管理员登录
     * 新增类目
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(1)
    public void postCategoryTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"name\": \"测试一级分类\",\"commissionRatio\": 0}";
        String responseString =new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPCATEGORY, 0, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试一级分类")
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        CategoryRetVo vo = JacksonUtil.parseObject(responseString, "data", CategoryRetVo.class);
        this.categoryId1 = vo.getId();
        this.mallClient.get().uri(PARENTCATEGORY,this.categoryId1)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

        this.mallClient.get().uri(SUBCATEGORY, 0)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.name == '测试一级分类')].id").isEqualTo(this.categoryId1.intValue())
                .jsonPath("$.data[?(@.name == '测试一级分类')].commissionRatio").isEqualTo(0);
    }

    /**
     * 不登录
     * @throws Exception
     */
    @Test
    public void postCategoryTest2() throws Exception {
        String roleJson = "{\"name\": \"测试一级分类\"}";
        this.gatewayClient.post().uri(SHOPCATEGORY, 0, 0)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台用户
     * @throws Exception
     */
    @Test
    public void postCategoryTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String roleJson = "{\"name\": \"测试一级分类\"}";
        this.gatewayClient.post().uri(SHOPCATEGORY, 0, 0)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /** 17
     *
     * 新增二级类目
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(2)
    public void postCategoryTest4() throws Exception {
        assertNotNull(this.categoryId1);
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"name\": \"测试二级分类\",\"commissionRatio\": 2}";
        String responseString =new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPCATEGORY, 0, this.categoryId1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试二级分类")
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        CategoryRetVo vo = JacksonUtil.parseObject(responseString, "data", CategoryRetVo.class);
        this.categoryId2 = vo.getId();
        this.mallClient.get().uri(PARENTCATEGORY,this.categoryId2)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.categoryId1.intValue());

        this.mallClient.get().uri(SUBCATEGORY, this.categoryId1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.name == '测试二级分类')].id").isEqualTo(this.categoryId2.intValue())
                .jsonPath("$.data[?(@.name == '测试二级分类')].commissionRatio").isEqualTo(2);
    }

    /** 18
     * 需管理员登录
     * 修改分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(3)
    public void putCategoryTest1() throws Exception {
        assertNotNull(this.categoryId1);
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"commissionRatio\": 1}";
        this.gatewayClient.put().uri(CATEGORY,0, this.categoryId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(SUBCATEGORY, 0)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.name == '测试一级分类')].id").isEqualTo(this.categoryId1.intValue())
                .jsonPath("$.data[?(@.name == '测试一级分类')].commissionRatio").isEqualTo(1);
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void putCategoryTest2() throws Exception {
        assertNotNull(this.categoryId1);
        String roleJson = "{\"commissionRatio\": 1}";
        this.gatewayClient.put().uri(CATEGORY, 0,1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台用户
     * @throws Exception
     */
    @Test
    public void putCategoryTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String roleJson = "{\"name\": \"测试一级分类\"}";
        this.gatewayClient.put().uri(CATEGORY, 0,1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /** 19
     * 需管理员登录
     * 删除分类-删除子分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(4)
    public void delCategoryTest1() throws Exception {
        assertNotNull(this.categoryId1);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(CATEGORY, 0,this.categoryId1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String roleJson = "{\"name\": \"测试一级分类\"}";
        this.gatewayClient.put().uri(CATEGORY, 0,this.categoryId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

    }

    /** 20
     * 需登录
     * 删除分类-删除分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    public void delCategoryTest2() throws Exception {
        this.gatewayClient.delete().uri(CATEGORY, 0,1)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台用户
     * @throws Exception
     */
    @Test
    public void delCategoryTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(CATEGORY, 0,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 删除一个不存在的id
     * @throws Exception
     */
    @Test
    public void delCategoryTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(CATEGORY, 0,109012)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /** 19
     * 孤儿分类
     **/
    @Test
    @Order(5)
    public void getOrphanTest1() throws Exception {
        assertNotNull(this.categoryId2);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ORPHAN, 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.name == '测试二级分类')].id").isEqualTo(this.categoryId2.intValue());
    }
    /** 20
     * 需登录
     * 删除分类-删除分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    public void getOrphanTest2() throws Exception {
        this.gatewayClient.get().uri(ORPHAN, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台用户
     * @throws Exception
     */
    @Test
    public void getOrphanTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(ORPHAN, 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
}
