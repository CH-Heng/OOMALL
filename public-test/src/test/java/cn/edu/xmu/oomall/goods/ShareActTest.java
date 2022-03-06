/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShareActTest extends BaseTestOomall {
    private static final String STATES = "/activity/shareactivities/states";
    private static final String SHAREACT = "/activity/shareactivities";
    private static final String SHAREACTID = "/activity/shareactivities/{id}";
    private static final String SHOPSHAREACT = "/activity/shops/{shopId}/shareactivities";
    private static final String SHOPSHAREACTONSALE = "/activity/shops/{shopId}/onsales/{pid}/shareactivities/{id}";
    private static final String SHOPSHAREACTID = "/activity/shops/{shopId}/shareactivities/{id}";
    private static final String ONLINE = "/activity/shops/{shopId}/shareactivities/{id}/online";
    private static final String OFFLINE = "/activity/shops/{shopId}/shareactivities/{id}/offline";
    private static final String PRODUCTID ="/goods/products/{id}";
    private static Integer shareActId1 = null;
    private static Integer shareActId2 = null;

    /**
     * 获得的所有状态
     * @throws Exception
     */
    @Test
    public void getState() throws Exception {
        this.mallClient.get().uri(STATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(3);
    }
    @Test
    public void getShareActDetail1() throws Exception {
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(SHAREACTID, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("分享活动1")
                .jsonPath("$.data.state").isEqualTo(1)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.strategy[0].quantity").isEqualTo(10)
                .jsonPath("$.data.strategy[0].percentage").isEqualTo(10);
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getShareActDetail2() throws Exception {
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(SHAREACTID, 10325)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    @Test
    @Order(0)
    public void getShopShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists();
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    public void postShareAct2() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        String json = "{\"name\": \"分享测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    public void postShareAct3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"分享测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 结束时间早于开始时间
     * @throws Exception
     */
    @Test
    public void postShareAct4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"分享测试1\", \"beginTime\": \"2023-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.LATE_BEGINTIME.getCode());
    }
    /**
     * 创建分享活动
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postShareAct5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"分享测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.shareActId1 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shareActId1)
                .jsonPath("$.data.name").isEqualTo("分享测试1")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    @Test
    @Order(2)
    public void getCustomerShareAct1() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(SHAREACT)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(7)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").doesNotExist();
    }
    @Test
    @Order(2)
    public void getShopShareAct2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, 23544558)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    public void getShareAct2() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    public void getShareAct3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void putShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"分享测试11\"}";
        this.gatewayClient.put().uri(SHOPSHAREACTID, 2,12435005)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putShareAct2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        String json = "{\"name\": \"分享测试11\"}";
        this.gatewayClient.put().uri(SHOPSHAREACTID,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putShareAct3() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"分享测试11\"}";
        this.gatewayClient.put().uri(SHOPSHAREACTID,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 结束时间早于开始时间
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putShareAct4() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2019-02-18T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPSHAREACTID,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.LATE_BEGINTIME.getCode());
    }
    /**
     * 修改分享活动
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putShareAct7() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"分享测试11\"}";
        this.gatewayClient.put().uri(SHOPSHAREACTID,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shareActId1)
                .jsonPath("$.data.name").isEqualTo("分享测试11")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postShareActOnsale1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1782, 9965822)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postShareActOnsale2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1782, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postShareActOnsale3() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1782, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 将销售加入分享
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postShareActOnsale4() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1782, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 将销售加入分享
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postShareActOnsale6() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1784, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 将销售加入分享
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postShareActOnsale7() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPSHAREACTONSALE, 2, 1799, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void onlineShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONLINE, 2,12435005)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    @Order(5)
    public void onlineShareAct2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(ONLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void onlineShareAct3() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    //上线前商品无分享
    @Test
    @Order(5)
    public void getProductDetail1() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3331)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3331)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1782)
                .jsonPath("$.data.name").isEqualTo("润口香甜王(70)")
                .jsonPath("$.data.originalPrice").isEqualTo(72329)
                .jsonPath("$.data.price").isEqualTo(30365)
                .jsonPath("$.data.quantity").isEqualTo(71)
                .jsonPath("$.data.weight").isEqualTo(50)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }
    //上线前商品无分享
    @Test
    @Order(5)
    public void getProductDetail2() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3333)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3333)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1784)
                .jsonPath("$.data.name").isEqualTo("葡萄风味烤香肠(2000)(4050)")
                .jsonPath("$.data.originalPrice").isEqualTo(94619)
                .jsonPath("$.data.price").isEqualTo(63576)
                .jsonPath("$.data.quantity").isEqualTo(49)
                .jsonPath("$.data.weight").isEqualTo(6)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }
    //上线前商品无分享
    @Test
    @Order(5)
    public void getProductDetail3() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3348)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3348)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1799)
                .jsonPath("$.data.name").isEqualTo("润口香甜王601010")
                .jsonPath("$.data.originalPrice").isEqualTo(40181)
                .jsonPath("$.data.price").isEqualTo(18165)
                .jsonPath("$.data.quantity").isEqualTo(62)
                .jsonPath("$.data.weight").isEqualTo(601010)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }
    /**
     * 上线分享活动
     * @throws Exception
     */
    @Test
    @Order(6)
    public void onlineShareAct4() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shareActId1)
                .jsonPath("$.data.name").isEqualTo("分享测试11")
                .jsonPath("$.data.state").isEqualTo( 1);
    }
    //上线后商品有分享
    @Test
    @Order(7)
    public void getProductDetail4() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3331)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3331)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1782)
                .jsonPath("$.data.name").isEqualTo("润口香甜王(70)")
                .jsonPath("$.data.originalPrice").isEqualTo(72329)
                .jsonPath("$.data.price").isEqualTo(30365)
                .jsonPath("$.data.quantity").isEqualTo(71)
                .jsonPath("$.data.weight").isEqualTo(50)
                .jsonPath("$.data.shareable").isEqualTo(true);
    }
    //上线后商品有分享
    @Test
    @Order(7)
    public void getProductDetail5() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3333)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3333)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1784)
                .jsonPath("$.data.name").isEqualTo("葡萄风味烤香肠(2000)(4050)")
                .jsonPath("$.data.originalPrice").isEqualTo(94619)
                .jsonPath("$.data.price").isEqualTo(63576)
                .jsonPath("$.data.quantity").isEqualTo(49)
                .jsonPath("$.data.weight").isEqualTo(6)
                .jsonPath("$.data.shareable").isEqualTo(true);
    }
    //上线后商品有分享
    @Test
    @Order(7)
    public void getProductDetail6() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3348)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3348)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1799)
                .jsonPath("$.data.name").isEqualTo("润口香甜王601010")
                .jsonPath("$.data.originalPrice").isEqualTo(40181)
                .jsonPath("$.data.price").isEqualTo(18165)
                .jsonPath("$.data.quantity").isEqualTo(62)
                .jsonPath("$.data.weight").isEqualTo(601010)
                .jsonPath("$.data.shareable").isEqualTo(true);
    }    @Test
    @Order(7)
    public void getCustomerShareAct2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(SHAREACT)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(8)
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offlineShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFLINE, 2,12435005)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    @Order(7)
    public void offlineShareAct2() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(7)
    public void offlineShareAct3() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 下线预售活动
     * @throws Exception
     */
    @Test
    @Order(8)
    public void offlineShareAct4() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.shareActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shareActId1)
                .jsonPath("$.data.name").isEqualTo("分享测试11")
                .jsonPath("$.data.state").isEqualTo( 2);
    }
    //线后商品无分享
    @Test
    @Order(9)
    public void getProductDetail7() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3331)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3331)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1782)
                .jsonPath("$.data.name").isEqualTo("润口香甜王(70)")
                .jsonPath("$.data.originalPrice").isEqualTo(72329)
                .jsonPath("$.data.price").isEqualTo(30365)
                .jsonPath("$.data.quantity").isEqualTo(71)
                .jsonPath("$.data.weight").isEqualTo(50)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }
    //线后商品无分享
    @Test
    @Order(9)
    public void getProductDetail8() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3333)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3333)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1784)
                .jsonPath("$.data.name").isEqualTo("葡萄风味烤香肠(2000)(4050)")
                .jsonPath("$.data.originalPrice").isEqualTo(94619)
                .jsonPath("$.data.price").isEqualTo(63576)
                .jsonPath("$.data.quantity").isEqualTo(49)
                .jsonPath("$.data.weight").isEqualTo(6)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }
    //线后商品无分享
    @Test
    @Order(9)
    public void getProductDetail9() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 3348)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(3348)
                .jsonPath("$.data.shop.id").isEqualTo(2)
                .jsonPath("$.data.onsaleId").isEqualTo(1799)
                .jsonPath("$.data.name").isEqualTo("润口香甜王601010")
                .jsonPath("$.data.originalPrice").isEqualTo(40181)
                .jsonPath("$.data.price").isEqualTo(18165)
                .jsonPath("$.data.quantity").isEqualTo(62)
                .jsonPath("$.data.weight").isEqualTo(601010)
                .jsonPath("$.data.shareable").isEqualTo(false);
    }

    /**
     * 下线后
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getCustomerShareAct3() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.customerLogin("customer1","123456");
        this.mallClient.get().uri(SHAREACT)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(7)
                .jsonPath("$.data.list[?(@.id == '8')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").doesNotExist();
    }
    /**
     * 创建分享活动
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postShareAct7() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"分享测试2\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.shareActId2 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.shareActId2)
                .jsonPath("$.data.name").isEqualTo("分享测试2")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    @Test
    @Order(11)
    public void getShopShareAct3() throws Exception {
        assertNotNull(this.shareActId1);
        assertNotNull(this.shareActId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(5)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId2 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delShareAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPSHAREACTID, 2,12435005)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    @Order(11)
    public void delShareAct2() throws Exception {
        assertNotNull(this.shareActId2);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.delete().uri(SHOPSHAREACTID,2, this.shareActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(11)
    public void delShareAct3() throws Exception {
        assertNotNull(this.shareActId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPSHAREACTID,2, this.shareActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 删除预售活动
     * @throws Exception
     */
    @Test
    @Order(12)
    public void delShareAct4() throws Exception {
        assertNotNull(this.shareActId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPSHAREACTID,2, this.shareActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPSHAREACTID, 2, this.shareActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    @Test
    @Order(13)
    public void getShopShareAct4() throws Exception {
        assertNotNull(this.shareActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPSHAREACT, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.shareActId1 +"')]").exists();
    }
}
