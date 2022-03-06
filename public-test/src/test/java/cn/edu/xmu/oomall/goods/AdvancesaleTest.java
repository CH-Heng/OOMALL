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

@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdvancesaleTest extends BaseTestOomall {
    private static final String STATES = "/activity/advancesales/states";
    private static final String ADVSALE = "/activity/advancesales";
    private static final String ADVSALEID = "/activity/advancesales/{id}";
    private static final String SHOPADV = "/activity/shops/{shopId}/advancesales";
    private static final String SHOPPRODADV = "/activity/shops/{shopId}/products/{id}/advancesales";
    private static final String SHOPADVID = "/activity/shops/{shopId}/advancesales/{id}";
    private static final String ONLINE = "/activity/shops/{shopId}/advancesales/{id}/online";
    private static final String OFFLINE = "/activity/shops/{shopId}/advancesales/{id}/offline";
    private static final String PRODUCTID ="/goods/products/{id}";

    private static Integer advId1 = null;
    private static Integer advId2 = null;
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
    @Order(0)
    public void getShopAdvSale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPADV, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '8')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postAdvSale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 12435005)
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
    public void postAdvSale2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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
    public void postAdvSale3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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
    public void postAdvSale4() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2019-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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
     * 尾款支付时间晚于活动结束时间
     * @throws Exception
     */
    @Test
    public void postAdvSale5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-04-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.ACT_LATE_PAYTIME.getCode());
    }
    /**
     * 尾款支付时间早于活动开始时间
     * @throws Exception
     */
    @Test
    public void postAdvSale6() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2023-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2012-04-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.ACT_EARLY_PAYTIME.getCode());
    }
    /**
     * 创建预售活动
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postAdvSale7() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2022-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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

        this.advId1 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.advId1)
                .jsonPath("$.data.name").isEqualTo("预售活动1")
                .jsonPath("$.data.state").isEqualTo(0);
    }
    /**
     * 商品销售时间冲突
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postAdvSale8() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2022-01-10T20:38:20.000+08:00\", \"endTime\": \"2022-03-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动1\",\"payTime\": \"2022-02-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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
    @Test
    @Order(2)
    public void getCustomerAdvSale1() throws Exception {
        this.mallClient.get().uri(ADVSALE)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(6)
                .jsonPath("$.data.list[?(@.id == '2')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '8')]").exists();
    }
    @Test
    @Order(2)
    public void getShopAdvSale2() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPADV, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '8')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.advId1+"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getAdvSale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPADVID, 1, 23544558)
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
    public void getAdvSale2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.get().uri(SHOPADVID, 1, 8)
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
    public void getAdvSale3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPADVID, 1, 8)
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
    public void putAdvSale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 150000}";
        this.gatewayClient.put().uri(SHOPADVID, 1,12435005)
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
    public void putAdvSale2() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"price\": 150000}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
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
    public void putAdvSale3() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"price\": 150000}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
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
    public void putAdvSale4() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2019-02-18T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
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
     * 尾款支付时间晚于活动结束时间
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putAdvSale5() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"endTime\": \"2012-02-18T20:38:20.000+08:00\", \"payTime\": \"2022-04-25T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.ACT_LATE_PAYTIME.getCode());
    }
    /**
     * 尾款支付时间早于活动开始时间
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putAdvSale6() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"beginTime\": \"2021-12-10T20:38:20.000+08:00\",\"payTime\": \"2012-04-25T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.ACT_EARLY_PAYTIME.getCode());
    }
    /**
     * 修改预售活动
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putAdvSale7() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 150000}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.advId1)
                .jsonPath("$.data.name").isEqualTo("预售活动1")
                .jsonPath("$.data.price").isEqualTo(150000)
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(100);
    }
    /**
     * 商品销售时间冲突
     * @throws Exception
     */
    @Test
    @Order(4)
    public void putAdvSale8() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"beginTime\": \"2022-01-10T20:38:20.000+08:00\", \"endTime\": \"2022-03-18T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPADVID,1, this.advId1)
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
     * 未上线销售
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getProductDetail1() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5005)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5005)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("阳阳红油豆瓣")
                .jsonPath("$.data.originalPrice").isEqualTo(36336)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void onlineAdvSale1() throws Exception {
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
    @Order(4)
    public void onlineAdvSale2() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.advId1)
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
    public void onlineAdvSale3() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.advId1)
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
     * 上线预售活动
     * @throws Exception
     */
    @Test
    @Order(5)
    public void onlineAdvSale4() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.advId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.advId1)
                .jsonPath("$.data.name").isEqualTo("预售活动1")
                .jsonPath("$.data.price").isEqualTo(150000)
                .jsonPath("$.data.state").isEqualTo(1)
                .jsonPath("$.data.quantity").isEqualTo(100);
    }
    /**
     * 上线销售
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getProductDetail2() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5005)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5005)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("阳阳红油豆瓣")
                .jsonPath("$.data.originalPrice").isEqualTo(36336)
                .jsonPath("$.data.price").isEqualTo(150000)
                .jsonPath("$.data.quantity").isEqualTo(100)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    @Test
    @Order(6)
    public void getCustomerAdvSale2() throws Exception {
        assertNotNull(this.advId1);
        this.mallClient.get().uri(ADVSALE)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(7)
                .jsonPath("$.data.list[?(@.id == '2')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.advId1+"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offlineAdvSale1() throws Exception {
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
    @Order(6)
    public void offlineAdvSale2() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.advId1)
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
    @Order(6)
    public void offlineAdvSale3() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.advId1)
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
    @Order(7)
    public void offlineAdvSale4() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFLINE,1, this.advId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.advId1)
                .jsonPath("$.data.name").isEqualTo("预售活动1")
                .jsonPath("$.data.price").isEqualTo(150000)
                .jsonPath("$.data.state").isEqualTo(2)
                .jsonPath("$.data.quantity").isEqualTo(100);
    }
    /**
     * 下线销售
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getProductDetail3() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 5005)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(5005)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("阳阳红油豆瓣")
                .jsonPath("$.data.originalPrice").isEqualTo(36336)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    @Test
    @Order(8)
    public void getCustomerAdvSale3() throws Exception {
        assertNotNull(this.advId1);
        this.mallClient.get().uri(ADVSALE)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(6)
                .jsonPath("$.data.list[?(@.id == '2')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '8')]").exists();
    }
    /**
     * 创建预售活动
     * @throws Exception
     */
    @Test
    @Order(8)
    public void postAdvSale9() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 100000, \"beginTime\": \"2023-12-10T20:38:20.000+08:00\", \"endTime\": \"2025-02-18T20:38:20.000+08:00\", " +
                "\"quantity\": 100, \"name\": \"预售活动2\",\"payTime\": \"2024-01-25T20:38:20.000+08:00\", \"advancePayPrice\": 10000 }";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPPRODADV, 1, 5005)
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

        this.advId2 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.advId2)
                .jsonPath("$.data.name").isEqualTo("预售活动2")
                .jsonPath("$.data.state").isEqualTo(0);
    }
    @Test
    @Order(9)
    public void getShopAdvSale3() throws Exception {
        assertNotNull(this.advId1);
        assertNotNull(this.advId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPADV, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '8')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.advId1+"')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.advId2+"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delAdvSale1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPADVID, 1,12435005)
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
    public void dellineAdvSale2() throws Exception {
        assertNotNull(this.advId2);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(SHOPADVID,1, this.advId2)
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
    public void dellineAdvSale3() throws Exception {
        assertNotNull(this.advId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPADVID,1, this.advId2)
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
    public void dellineAdvSale4() throws Exception {
        assertNotNull(this.advId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPADVID,1, this.advId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPADVID, 1, this.advId2)
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
    public void getShopAdvSale4() throws Exception {
        assertNotNull(this.advId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPADV, 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '8')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.advId1+"')]").exists();
    }
}
