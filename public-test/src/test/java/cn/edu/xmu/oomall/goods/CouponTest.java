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
public class CouponTest extends BaseTestOomall {
    private static final String STATES = "/coupon/couponactivities/states";
    private static final String COUPON = "/coupon/couponactivities";
    private static final String COUPONID = "/coupon/couponactivities/{id}";
    private static final String SHOPCOUPON = "/coupon/shops/{shopId}/couponactivities";
    private static final String SHOPCOUPONONSALE = "/coupon/shops/{shopId}/couponactivities/{id}/onsales/{pid}";
    private static final String SHOPCOUPONID = "/coupon/shops/{shopId}/couponactivities/{id}";
    private static final String ONLINE = "/coupon/shops/{shopId}/couponactivities/{id}/online";
    private static final String OFFLINE = "/coupon/shops/{shopId}/couponactivities/{id}/offline";
    private static final String LISTPRODUCT ="/coupon/couponactivities/{id}/products";
    private static final String LISTCOUPON ="/coupon/products/{id}/couponactivities";

    private static Integer couponActId1 = null;
    private static Integer couponActId2 = null;

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
    public void getShopCouponAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPCOUPON, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(6)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '8')]").exists();
    }
    /**
     * 权限不够
     * @throws Exception
     */
    @Test
    public void postCouponAct2() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        String json = "{\"name\": \"优惠测试1\", \"quantity\": 1000, \"quantityType\": 1, \"validTerm\": 0, \"couponTime\": \"2021-12-10T20:38:20.000+08:00\"," +
                "  \"beginTime\": \"2021-12-10T20:38:20.000+08:00\",  \"endTime\": \"2022-12-10T20:38:20.000+08:00\"," +
                "  \"strategy\": \"{\"value\":1000,\"className\":\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponDiscount\"," +
                "\"couponLimitation\":{\"value\":10000,\"className\":\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponLimitation\"}}\"," +
                "\"numKey\": 1}";
        this.gatewayClient.post().uri(SHOPCOUPON, 2)
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
    public void postCouponAct3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"优惠测试1\", \"quantity\": 1000, \"quantityType\": 1, \"validTerm\": 0, \"couponTime\": \"2021-12-10T20:38:20.000+08:00\"," +
                "  \"beginTime\": \"2021-12-10T20:38:20.000+08:00\",  \"endTime\": \"2022-12-10T20:38:20.000+08:00\"," +
                "  \"strategy\": \"{\"value\":1000,\"className\":\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponDiscount\"," +
                "\"couponLimitation\":{\"value\":10000,\"className\":\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponLimitation\"}}\"," +
                "\"numKey\": 1}";
        this.gatewayClient.post().uri(SHOPCOUPON, 2)
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
    public void postCouponAct4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"优惠测试1\", \"quantity\": 1000, \"quantityType\": 1, \"validTerm\": 0, \"couponTime\": \"2021-12-10T20:38:20.000+08:00\"," +
                "  \"beginTime\": \"2021-12-10T20:38:20.000+08:00\",  \"endTime\": \"2020-12-10T20:38:20.000+08:00\"," +
                "  \"strategy\": \"{\\\"value\\\":1000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponDiscount\\\"," +
                "\\\"couponLimitation\\\":{\\\"value\\\":10000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponLimitation\\\"}}\"," +
                "\"numKey\": 1}";
        this.gatewayClient.post().uri(SHOPCOUPON, 2)
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
     * 创建优惠活动
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postCouponAct5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\n" +
                "    \"name\": \"优惠测试1\",\n" +
                "    \"quantity\": 1000,\n" +
                "    \"quantityType\": 1,\n" +
                "    \"validTerm\": 0,\n" +
                "    \"couponTime\": \"2021-12-10T20:38:20.000+08:00\",\n" +
                "    \"beginTime\": \"2021-12-10T20:38:20.000+08:00\",\n" +
                "    \"endTime\": \"2022-12-10T20:38:20.000+08:00\",\n" +
                "    \"strategy\": \"{\\\"value\\\":1000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponDiscount\\\",\\\"couponLimitation\\\":{\\\"value\\\":10000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponLimitation\\\"}}\",\n" +
                "    \"numKey\": 1\n" +
                "}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPCOUPON, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.couponActId1 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.couponActId1)
                .jsonPath("$.data.name").isEqualTo("优惠测试1")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    @Test
    @Order(2)
    public void getCustomerCouponAct1() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(COUPON+"?page=1&pageSize=30")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(11)
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '8')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").doesNotExist();
    }
    @Test
    @Order(2)
    public void getShopCouponAct2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPCOUPON, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(7)
                .jsonPath("$.data.list[?(@.id == '9')]").exists()
                .jsonPath("$.data.list[?(@.id == '11')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getCouponAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPCOUPONID, 2, 23544558)
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
    public void getCouponAct2() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.get().uri(SHOPCOUPONID, 2, 1)
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
    public void getCouponAct3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPCOUPONID, 2, 1)
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
    public void putCouponAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"优惠测试11\"}";
        this.gatewayClient.put().uri(SHOPCOUPONID, 2,12435005)
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
    public void putCouponAct2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        String json = "{\"name\": \"优惠测试11\"}";
        this.gatewayClient.put().uri(SHOPCOUPONID,2, this.couponActId1)
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
    public void putCouponAct3() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"优惠测试11\"}";
        this.gatewayClient.put().uri(SHOPCOUPONID,2, this.couponActId1)
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
    public void putCouponAct4() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"beginTime\": \"2021-12-10T20:38:20.000+08:00\", \"endTime\": \"2019-02-18T20:38:20.000+08:00\"}";
        this.gatewayClient.put().uri(SHOPCOUPONID,2, this.couponActId1)
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
     * 修改优惠活动
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putCouponAct7() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"优惠测试11\"}";
        this.gatewayClient.put().uri(SHOPCOUPONID,2, this.couponActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.couponActId1)
                .jsonPath("$.data.name").isEqualTo("优惠测试11")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postCouponActOnsale1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, 9965822, 9)
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
    public void postCouponActOnsale2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, this.couponActId1, 9)
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
    public void postCouponActOnsale3() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, this.couponActId1, 9)
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
     * 将销售加入优惠
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postCouponActOnsale4() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, this.couponActId1, 9)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 将销售加入优惠
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postCouponActOnsale6() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, this.couponActId1, 11)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 将销售加入优惠
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postCouponActOnsale7() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(SHOPCOUPONONSALE, 2, this.couponActId1, 33)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void onlineCouponAct1() throws Exception {
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
    public void onlineCouponAct2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(ONLINE,2, this.couponActId1)
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
    public void onlineCouponAct3() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONLINE,1, this.couponActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    //活动中的商品
    @Test
    @Order(6)
    public void getCouponProduct1() throws Exception {
        assertNotNull(this.couponActId1);
        this.mallClient.get().uri(LISTPRODUCT, this.couponActId1)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == 1558)].name").isEqualTo("奥利奥树莓蓝莓")
                .jsonPath("$.data.list[?(@.id == 1560)].name").isEqualTo("奥利奥桶装巧力味")
                .jsonPath("$.data.list[?(@.id == 1582)].name").isEqualTo("加加老王500");
    }
    //上线前活动不会列出
    @Test
    @Order(6)
    public void getProductCoupon1() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1558)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    //上线前活动不会列出
    @Test
    @Order(6)
    public void getProductCoupon2() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1560)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    //上线前活动不会列出
    @Test
    @Order(6)
    public void getProductCoupon3() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1582)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    /**
     * 上线优惠活动
     * @throws Exception
     */
    @Test
    @Order(7)
    public void onlineCouponAct4() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONLINE,2, this.couponActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.couponActId1)
                .jsonPath("$.data.name").isEqualTo("优惠测试11")
                .jsonPath("$.data.state").isEqualTo( 1);
    }
    //上线后列出活动
    @Test
    @Order(8)
    public void getProductCoupon4() throws Exception {
        assertNotNull(this.couponActId1);
        this.mallClient.get().uri(LISTCOUPON, 1558)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1+"')].name").isEqualTo("优惠测试11");
    }
    //上线后列出活动
    @Test
    @Order(8)
    public void getProductCoupon5() throws Exception {
        assertNotNull(this.couponActId1);
        this.mallClient.get().uri(LISTCOUPON, 1560)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1+"')].name").isEqualTo("优惠测试11");
    }
    //上线后列出活动
    @Test
    @Order(8)
    public void getProductCoupon6() throws Exception {
        assertNotNull(this.couponActId1);
        this.mallClient.get().uri(LISTCOUPON, 1582)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1+"')].name").isEqualTo("优惠测试11");
    }
    @Test
    @Order(8)
    public void getCustomerCouponAct2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(COUPON+"?page=1&pageSize=30")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(12)
                .jsonPath("$.data.list[?(@.id == '13')]").exists()
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offlineCouponAct1() throws Exception {
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
    @Order(8)
    public void offlineCouponAct2() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.couponActId1)
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
    @Order(8)
    public void offlineCouponAct3() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.couponActId1)
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
    @Order(9)
    public void offlineCouponAct4() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFLINE,2, this.couponActId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.couponActId1)
                .jsonPath("$.data.name").isEqualTo("优惠测试11")
                .jsonPath("$.data.state").isEqualTo( 2);
    }
    //下线后商品无活动
    @Test
    @Order(10)
    public void getProductCoupon7() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1558)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    //下线后商品无活动
    @Test
    @Order(10)
    public void getProductCoupon8() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1560)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    //下线后商品无活动
    @Test
    @Order(10)
    public void getProductCoupon9() throws Exception {
        this.mallClient.get().uri(LISTCOUPON, 1582)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    /**
     * 下线后
     * @throws Exception
     */
    @Test
    @Order(10)
    public void getCustomerCouponAct3() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.customerLogin("customer1","123456");
        this.mallClient.get().uri(COUPON+"?page=1&pageSize=30")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(11)
                .jsonPath("$.data.list[?(@.id == '3')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").doesNotExist();
    }
    /**
     * 创建优惠活动
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postCouponAct7() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"name\": \"优惠测试2\", \"quantity\": 2000, \"quantityType\": 1, \"validTerm\": 0, \"couponTime\": \"2021-12-10T20:38:20.000+08:00\"," +
                "  \"beginTime\": \"2021-12-10T20:38:20.000+08:00\",  \"endTime\": \"2022-12-10T20:38:20.000+08:00\"," +
                "  \"strategy\": \"{\\\"value\\\":1000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponDiscount\\\"," +
                "\\\"couponLimitation\\\":{\\\"value\\\":10000,\\\"className\\\":\\\"cn.edu.xmu.oomall.coupon.model.bo.strategy.impl.PriceCouponLimitation\\\"}}\"," +
                "\"numKey\": 1}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(SHOPCOUPON, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.couponActId2 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.couponActId2)
                .jsonPath("$.data.name").isEqualTo("优惠测试2")
                .jsonPath("$.data.state").isEqualTo( 0);
    }
    @Test
    @Order(11)
    public void getShopCouponAct3() throws Exception {
        assertNotNull(this.couponActId1);
        assertNotNull(this.couponActId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPCOUPON, 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(8)
                .jsonPath("$.data.list[?(@.id == '1')]").exists()
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId2 +"')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delCouponAct1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPCOUPONID, 2,12435005)
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
    public void delCouponAct2() throws Exception {
        assertNotNull(this.couponActId2);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.delete().uri(SHOPCOUPONID,2, this.couponActId2)
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
    public void delCouponAct3() throws Exception {
        assertNotNull(this.couponActId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(SHOPCOUPONID,2, this.couponActId2)
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
    public void delCouponAct4() throws Exception {
        assertNotNull(this.couponActId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(SHOPCOUPONID,2, this.couponActId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPCOUPONID, 2, this.couponActId2)
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
    public void getShopCouponAct4() throws Exception {
        assertNotNull(this.couponActId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPCOUPON, 2)
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
                .jsonPath("$.data.list[?(@.id == '10')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId1 +"')]").exists()
                .jsonPath("$.data.list[?(@.id == '"+this.couponActId2 +"')]").doesNotExist();
    }
}
