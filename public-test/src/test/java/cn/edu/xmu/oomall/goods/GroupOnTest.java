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
public class GroupOnTest extends BaseTestOomall {
    private static final String STATES = "/activity/groupons/states";
    private static final String GROUPON = "/activity/groupons";
    private static final String GROUPONID = "/activity/groupons/{id}";
    private static final String SHOPGROUPON = "/activity/shops/{shopId}/groupons";
    private static final String SHOPGROUPONONSALE = "/activity/shops/{shopId}/products/{pid}/groupons/{id}/onsales";
    private static final String SHOPGROUPONID = "/activity/shops/{shopId}/groupons/{id}";
    private static final String ONLINE = "/activity/shops/{shopId}/groupons/{id}/online";
    private static final String OFFLINE = "/activity/shops/{shopId}/groupons/{id}/offline";
    //综合测试
    private static final String FINISH = "/activity/shops/{shopId}/groupons/{id}/finish";
    private static final String PRODUCTID ="/goods/products/{id}";
    private static Integer grouponId1 = null;
    private static Integer grouponId2 = null;
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
    public void getGropuonDetail1() throws Exception {
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(GROUPONID, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("团购活动1")
                .jsonPath("$.data.shop.id").isEqualTo(3);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getGropuonDetail2() throws Exception {
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(GROUPONID, 10325)
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
    public void getShopGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPGROUPON, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '4')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists();
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    public void postGroupon2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"name\": \"团购测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPGROUPON, 1)
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
    public void postGroupon3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"团购测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPGROUPON, 1)
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
    public void postGroupon4() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"团购测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2012-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        this.gatewayClient.post().uri(SHOPGROUPON, 1)
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
     * 创建团购活动
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postGroupon5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"团购测试1\", \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPGROUPON, 1)
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

        this.grouponId1 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.grouponId1)
                .jsonPath("$.data.name").isEqualTo("团购测试1")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    @Test
    @Order(2)
    public void getCustomerGroupon1() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(GROUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(8)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").doesNotExist();
    }
    @Test
    @Order(2)
    public void getShopGroupon2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPGROUPON, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4)
                .jsonPath("$.data.list[?(@.id == '4')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPGROUPONID, 1, 23544558)
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
    public void getGroupon2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.get().uri(SHOPGROUPONID, 1, 4)
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
    public void getGroupon3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPGROUPONID, 1, 4)
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
    public void putGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"团购测试11\"}";
        this.gatewayClient.put().uri(SHOPGROUPONID, 1,12435005)
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
    public void putGroupon2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"name\": \"团购测试11\"}";
        this.gatewayClient.put().uri(SHOPGROUPONID,1, this.grouponId1)
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
    public void putGroupon3() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"团购测试11\"}";
        this.gatewayClient.put().uri(SHOPGROUPONID,1, this.grouponId1)
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
    public void putGroupon4() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2019-02-18T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPGROUPONID,1, this.grouponId1)
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
     * 修改团购活动
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putGroupon7() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"团购测试11\"}";
        this.gatewayClient.put().uri(SHOPGROUPONID,1, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.grouponId1)
                .jsonPath("$.data.name").isEqualTo("团购测试11")
                .jsonPath("$.data.state").isEqualTo(0);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postGrouponOnsale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 30000,\"quantity\": 100, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 5010, 9965822)
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
    @Order(4)
    public void postGrouponOnsale2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"price\": 30000,\"quantity\": 100, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 5010, this.grouponId1)
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
    @Order(4)
    public void postGrouponOnsale3() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"price\": 30000,\"quantity\": 100, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 5010, this.grouponId1)
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
     * 时间冲突
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postGrouponOnsale4() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 30000,\"quantity\": 100, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 4893, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.GOODS_PRICE_CONFLICT.getCode());
    }
    /**
     * 将销售加入团购
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postGrouponOnsale6() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 30000,\"quantity\": 100, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 5010, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 将销售加入团购
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postGrouponOnsale7() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 80000,\"quantity\": 200, \"numKey\": 1, \"maxQuantity\": 2}";
        this.gatewayClient.post().uri(SHOPGROUPONONSALE, 1, 5015, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
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
    public void onlineGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONLINE, 1,12435005)
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
    public void onlineGroupon2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.grouponId1)
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
    public void onlineGroupon3() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    //上线前商品无有效销售
    @Test
    @Order(5)
    public void getProductDetail1() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5010)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5010)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("好口福芝麻香")
                .jsonPath("$.data.originalPrice").isEqualTo(42669)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(200);
    }
    //上线前商品无有效销售
    @Test
    @Order(5)
    public void getProductDetail2() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5015)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5015)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("90大富贵面")
                .jsonPath("$.data.originalPrice").isEqualTo(96417)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(50);
    }
    /**
     * 上线团购活动
     * @throws Exception
     */
    @Test
    @Order(6)
    public void onlineGroupon4() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.grouponId1)
                .jsonPath("$.data.name").isEqualTo("团购测试11")
                .jsonPath("$.data.state").isEqualTo(1);
    }
    //增加后商品有效销售
    @Test
    @Order(7)
    public void getProductDetail3() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5010)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5010)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("好口福芝麻香")
                .jsonPath("$.data.originalPrice").isEqualTo(42669)
                .jsonPath("$.data.price").isEqualTo(30000)
                .jsonPath("$.data.quantity").isEqualTo(100)
                .jsonPath("$.data.weight").isEqualTo(200);
    }
    @Test
    @Order(7)
    public void getProductDetail4() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5015)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5015)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("90大富贵面")
                .jsonPath("$.data.originalPrice").isEqualTo(96417)
                .jsonPath("$.data.price").isEqualTo(80000)
                .jsonPath("$.data.quantity").isEqualTo(200)
                .jsonPath("$.data.weight").isEqualTo(50);
    }
    @Test
    @Order(7)
    public void getCustomerGroupon2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(GROUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(9)
                .jsonPath("$.data.list[?(@.id == '3')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offlineGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFLINE, 1,12435005)
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
    public void offlineGroupon2() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.grouponId1)
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
    public void offlineGroupon3() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.grouponId1)
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
    public void offlineGroupon4() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.grouponId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.grouponId1)
                .jsonPath("$.data.name").isEqualTo("团购测试11")
                .jsonPath("$.data.state").isEqualTo(2);
    }
    //下线后商品无有效销售
    @Test
    @Order(9)
    public void getProductDetail5() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5010)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5010)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("好口福芝麻香")
                .jsonPath("$.data.originalPrice").isEqualTo(42669)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(200);
    }
    //下线后商品无有效销售
    @Test
    @Order(9)
    public void getProductDetail6() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5015)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5015)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("90大富贵面")
                .jsonPath("$.data.originalPrice").isEqualTo(96417)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(50);
    }

    /**
     * 下线后
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getCustomerGroupon3() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.customerLogin("customer1","123456");
        this.mallClient.get().uri(GROUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(8)
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").doesNotExist();

    }
    /**
     * 创建预售活动
     * @throws Exception
     */
    @Test
    @Order(8)
    public void postGroupon7() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"团购测试2\", \"beginTime\": \"2022-12-10T20:38:20.000+08:00\", \"endTime\": \"2023-02-18T20:38:20.000+08:00\"," +
                "\"strategy\": [" +
                "{ \"quantity\": 100, \"percentage\": 1}," +
                "{ \"quantity\": 200, \"percentage\": 2}," +
                "{ \"quantity\": 300, \"percentage\": 2}" +
                "]}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPGROUPON, 1)
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

        this.grouponId2 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.grouponId2)
                .jsonPath("$.data.name").isEqualTo("团购测试2")
                .jsonPath("$.data.state").isEqualTo(0);
    }
    @Test
    @Order(9)
    public void getShopGroupon3() throws Exception {
        assertNotNull(this.grouponId1);
        assertNotNull(this.grouponId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPGROUPON, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(5)
                .jsonPath("$.data.list[?(@.id == '4')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId2 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delGroupon1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPGROUPONID, 1,12435005)
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
    @Order(9)
    public void delGroupon2() throws Exception {
        assertNotNull(this.grouponId2);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(SHOPGROUPONID,1, this.grouponId2)
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
    @Order(9)
    public void delGroupon3() throws Exception {
        assertNotNull(this.grouponId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPGROUPONID,1, this.grouponId2)
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
    @Order(10)
    public void delGroupon4() throws Exception {
        assertNotNull(this.grouponId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPGROUPONID,1, this.grouponId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPGROUPONID, 1, this.grouponId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    @Test
    @Order(11)
    public void getShopGroupon4() throws Exception {
        assertNotNull(this.grouponId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPGROUPON, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4)
                .jsonPath("$.data.list[?(@.id == '4')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.grouponId1+"')]").exists();
    }
}
