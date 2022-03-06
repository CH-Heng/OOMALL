package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.goods.vo.ShopAccountRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.goods.vo.ShopSimpleRetVo;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShopTest extends BaseTestOomall {

    private static final String SHOPSTATE = "/shop/shops/states";
    private static final String SHOPID = "/shop/shops/{id}";
    private static final String ADMINSHOP = "/shop/shops/{id}/shops";
    private static final String POSTSHOP = "/shop/shops";
    private static final String SELFUSER = "/privilege/self/users";
    private static final String AUDITSHOP = "/shop/shops/{shopId}/newshops/{id}/audit";
    private static final String ONLINESHOP = "/shop/shops/{id}/online";
    private static final String OFFLINESHOP = "/shop/shops/{id}/offline";
    private static final String ACCOUNTSHOP = "/shop/shops/{id}/accounts";
    private static final String ACCOUNT = "/shop/shops/{did}/accounts/{id}";
    private static Long shop1Id = null;
    private static Long accountId = null;

    @Test
    public void getShopStateTest1() throws Exception{
        this.mallClient.get().uri(SHOPSTATE)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(4);
    }

    @Test
    public void getShopTest1() throws Exception{
        this.mallClient.get().uri(SHOPID,1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("OOMALL自营商铺");
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void getShopTest2() throws Exception {
        this.mallClient.get().uri(SHOPID,1221333)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getAdminShopTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '1')].name").isEqualTo("OOMALL自营商铺")
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '5')].name").isEqualTo("坚持就是胜利")
                .jsonPath("$.data.list[?(@.id == '5')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '8')].name").isEqualTo("商铺8")
                .jsonPath("$.data.list[?(@.id == '8')].state").isEqualTo(0);
    }

    /**
     * 非平台管理员
     * @throws Exception
     */
    @Test
    public void getAdminShopTest2() throws Exception{
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 无店铺用户
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postShopTest1() throws Exception{
        String token = this.adminLogin("jxy123", "123456");
        String json = "{\"name\":\"新开铺\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(POSTSHOP)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("新开铺")
                .returnResult().getResponseBody()),"UTF-8");

        ShopSimpleRetVo vo = JacksonUtil.parseObject(ret, "data", ShopSimpleRetVo.class);
        this.shop1Id = vo.getId();

        this.gatewayClient.get().uri(SELFUSER)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.departId").isEqualTo(this.shop1Id.intValue());

        String token1 = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺')].state").isEqualTo(0);
    }

    /**
     * 有店铺用户
     * @throws Exception
     */
    @Test
    public void postShopTest2() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\":\"新开铺\"}";
        this.gatewayClient.post().uri(POSTSHOP)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.SHOP_USER_HASSHOP.getCode());

        this.gatewayClient.get().uri(SELFUSER)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.departId").isEqualTo(1);
    }

    /**
     * 修改审核未通过的商铺的名字， 无权限
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(3)
    public void putShopTest0() throws Exception{
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("jxy123","123456");
        String requestJson = "{\"name\": \"新开铺_jxy123\",\"state\":4}";
        this.gatewayClient.put().uri(SHOPID,this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 修改审核未通过的商铺的名字
     * 企图修改商铺的状态
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(3)
    public void putShopTest1() throws Exception{
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin","123456");
        String requestJson = "{\"name\": \"新开铺_jxy123\",\"state\":4}";
        this.gatewayClient.put().uri(SHOPID,this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(SHOPID,this.shop1Id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.name").isEqualTo("新开铺_jxy123");
    }
    /**
     * 修改商铺的名字为空
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    public void putShopTest2() throws Exception {
        String shopToken = this.adminLogin("13088admin","123456");
        String requestJson = "{\"name\": \"  \"}";
        this.gatewayClient.put().uri(SHOPID,this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * 企图修改不属于自己的商铺
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    @Order(3)
    public void putShopTest3() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        String requestJson = "{\"name\": \"别人的店铺\"}";
        this.gatewayClient.put().uri(SHOPID, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void putShopTest4() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        String requestJson = "{\"name\": \"别人的店铺\"}";
        this.gatewayClient.put().uri(SHOPID, 122200)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 平台管理员审核不通过
     */
    @Test
    @Order(4)
    public void auditShopTest1() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        String requestJson = "{\"conclusion\": false}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(0);
    }


    /**
     * 非平台管理员审核通过
     */
    @Test
    @Order(4)
    public void auditShopTest2() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        String requestJson = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(0);
    }

    /**
     * 未登录
     */
    @Test
    @Order(4)
    public void auditShopTest3() throws Exception {
        assertNotNull(this.shop1Id);
        String requestJson = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, this.shop1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(0);
    }

    /**
     * 平台管理员上线未审核的店铺
     */
    @Test
    @Order(4)
    public void onlineShopTest0() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(ONLINESHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(0);
    }
    /**
     * 平台管理员下线未审核的店铺
     */
    @Test
    @Order(4)
    public void offlineShopTest0() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(OFFLINESHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(0);
    }

    /**
     * 平台管理员审核通过
     */
    @Test
    @Order(5)
    public void auditShopTest4() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        String requestJson = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺_jxy123')].state").isEqualTo(1);
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void auditShopTest5() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        String requestJson = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, 12200)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 修改审核通过的商铺的名字
     * 企图修改商铺的id
     */
    @Test
    @Order(6)
    public void putShopTest5() throws Exception{
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin","123456");
        String requestJson = "{\"name\": \"新开铺\",\"id\":1004}";
        this.gatewayClient.put().uri(SHOPID,this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(SHOPID,this.shop1Id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.name").isEqualTo("新开铺");
    }

    /**
     * 未登录
     */
    @Test
    @Order(7)
    public void onlineShopTest1() throws Exception {
        assertNotNull(this.shop1Id);
        this.gatewayClient.put().uri(ONLINESHOP, this.shop1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台管理员
     */
    @Test
    @Order(7)
    public void onlineShopTest2() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(ONLINESHOP, this.shop1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void onlineShopTest3() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(ONLINESHOP, 12200)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 平台管理员上线
     */
    @Test
    @Order(8)
    public void onlineShopTest4() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(ONLINESHOP, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺')].state").isEqualTo(2);
    }

    /**
     * 平台管理员审核通过已上线的店铺
     */
    @Test
    @Order(9)
    public void auditShopTest6() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        String requestJson = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(AUDITSHOP, 0, this.shop1Id)
                .header("authorization", shopToken)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺')].state").isEqualTo(2);
    }

    /**
     * 未登录
     */
    @Test
    @Order(9)
    public void offlineShopTest1() throws Exception {
        assertNotNull(this.shop1Id);
        this.gatewayClient.put().uri(OFFLINESHOP, this.shop1Id)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台管理员
     */
    @Test
    @Order(9)
    public void offlineShopTest2() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(OFFLINESHOP, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void offlineShopTest3() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(OFFLINESHOP, 123234)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 平台管理员下线
     */
    @Test
    @Order(10)
    public void offlineShopTest4() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(OFFLINESHOP, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '新开铺')].id").isEqualTo(this.shop1Id.intValue())
                .jsonPath("$.data.list[?(@.name == '新开铺')].state").isEqualTo(1);
    }

    /**
     * 未登录
     */
    @Test
    public void postAccountTest1() throws Exception {
        assertNotNull(this.shop1Id);
        String json = "{\"type\": 0, \"account\": \"112243424\", \"name\": \"JAVAEE\", \"priority\": 1}";
        this.gatewayClient.post().uri(ACCOUNTSHOP, this.shop1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台管理员
     */
    @Test
    @Order(11)
    public void postAccountTest2() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"type\": 0, \"account\": \"112243424\", \"name\": \"JAVAEE\", \"priority\": 1}";
        this.gatewayClient.post().uri(ACCOUNTSHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void postAccountTest3() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        String json = "{\"type\": 0, \"account\": \"112243424\", \"name\": \"JAVAEE\", \"priority\": 1}";
        this.gatewayClient.post().uri(ACCOUNTSHOP, 1233454)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     *  无权限增加account
     */
    @Test
    @Order(11)
    public void postAccountTest4() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("jxy123", "123456");
        String json = "{\"type\": 0, \"account\": \"112243424\", \"name\": \"JAVAEE\", \"priority\": 1}";
        this.gatewayClient.post().uri(ACCOUNTSHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     *  平台管理员增加账号
     */
    @Test
    @Order(11)
    public void postAccountTest5() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        String json = "{\"type\": 0, \"account\": \"112243424\", \"name\": \"JAVAEE\", \"priority\": 1}";
        this.gatewayClient.post().uri(ACCOUNTSHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ACCOUNTSHOP,this.shop1Id)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.type == 0)].name").isEqualTo("JAVAEE")
                .jsonPath("$.data[?(@.type == 0)].account").isEqualTo("112243424")
                .jsonPath("$.data.length()").isEqualTo(1);
    }

    /**
     *  平台管理员增加账号
     */
    @Test
    @Order(12)
    public void postAccountTest6() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        String json = "{\"type\": 1, \"account\": \"222243424\", \"name\": \"JAVAEE2\", \"priority\": 0}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(ACCOUNTSHOP, this.shop1Id)
                .header("authorization", shopToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        ShopAccountRetVo vo = JacksonUtil.parseObject(ret, "data", ShopAccountRetVo.class);
        this.accountId = vo.getId();

        this.gatewayClient.get().uri(ACCOUNTSHOP,this.shop1Id)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.type == 0)].name").isEqualTo("JAVAEE")
                .jsonPath("$.data[?(@.type == 0)].account").isEqualTo("112243424")
                .jsonPath("$.data[?(@.type == 1)].name").isEqualTo("JAVAEE2")
                .jsonPath("$.data[?(@.type == 1)].account").isEqualTo("222243424")
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    /**
     * 未登录
     */
    @Test
    public void delAccountTest1() throws Exception {
        assertNotNull(this.shop1Id);
        assertNotNull(this.accountId);
        this.gatewayClient.delete().uri(ACCOUNT, this.shop1Id, this.accountId)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台管理员
     */
    @Test
    @Order(13)
    public void delAccountTest2() throws Exception {
        assertNotNull(this.shop1Id);
        assertNotNull(this.accountId);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(ACCOUNT, this.shop1Id, this.accountId)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void delAccountTest3() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(ACCOUNT, 10, 12334523)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     *  无权限删除
     */
    @Test
    @Order(13)
    public void delAccountTest4() throws Exception {
        assertNotNull(this.shop1Id);
        assertNotNull(this.accountId);
        String shopToken = this.adminLogin("jxy123", "123456");
        this.gatewayClient.delete().uri(ACCOUNT, this.shop1Id, this.accountId)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     *  平台管理员删除账号
     */
    @Test
    @Order(14)
    public void delAccountTest5() throws Exception {
        assertNotNull(this.shop1Id);
        assertNotNull(this.accountId);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(ACCOUNT, this.shop1Id, this.accountId)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ACCOUNTSHOP,this.shop1Id)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.id == '"+this.accountId+"')]").doesNotExist();
    }

    /**
     * 未登录
     */
    @Test
    @Order(15)
    public void delShopTest1() throws Exception {
        assertNotNull(this.shop1Id);
        this.gatewayClient.delete().uri(SHOPID, this.shop1Id)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 非平台管理员
     */
    @Test
    @Order(15)
    public void dellineShopTest2() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(SHOPID, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不存在的商铺
     */
    @Test
    public void delShopTest3() throws Exception {
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(SHOPID, 18996)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     *  无权限删除
     */
    @Test
    @Order(15)
    public void delShopTest4() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("jxy123", "123456");
        this.gatewayClient.delete().uri(SHOPID, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     *  平台管理员关闭下线态商店
     */
    @Test
    @Order(16)
    public void delShopTest5() throws Exception {
        assertNotNull(this.shop1Id);
        String shopToken = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(SHOPID, this.shop1Id)
                .header("authorization", shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ADMINSHOP+"?page=1&pageSize=20",0)
                .header("authorization",shopToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.shop1Id+"')].state").isEqualTo(4);
    }
}
