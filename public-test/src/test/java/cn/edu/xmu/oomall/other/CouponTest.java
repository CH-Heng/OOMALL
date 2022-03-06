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

package cn.edu.xmu.oomall.other;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouponTest extends BaseTestOomall {
    private static final Integer RETRYTIME = 3;

    private static final String STATES = "/customer/coupons/states";
    private static final String COUPON = "/customer/coupons";
    private static final String POSTCOUPON = "/customer/couponactivities/{id}/coupons";

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
    /**
     * token不对
     *
     * @throws Exception
     */
    @Test
    public void postCoupon1() throws Exception {
        this.mallClient.post().uri(POSTCOUPON, 3)
                .header("authorization", "hellp")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 时间未到
     *
     * @throws Exception
     */
    @Test
    public void postCoupon2() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.post().uri(POSTCOUPON, 5)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.COUPON_NOTBEGIN.getCode());
    }
    /**
     * 时间已过
     *
     * @throws Exception
     */
    @Test
    public void postCoupon3() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.post().uri(POSTCOUPON, 6)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.COUPON_END.getCode());
    }
    /**
     * 已经领完
     *
     * @throws Exception
     */
    @Test
    public void postCoupon4() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.post().uri(POSTCOUPON, 4)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.COUPON_FINISH.getCode());
    }
    /**
     * 领总量控制优惠卷
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postCoupon5() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.get().uri(COUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);

        this.mallClient.post().uri(POSTCOUPON, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(1)
                .jsonPath("$.data[?(@.customerId == '1')].activityId").isEqualTo(2);
    }
    /**
     * 查询postCoupon5这张优惠卷
     *
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getCoupon1() throws Exception {

        String token = this.customerLogin("699275", "123456");
        int times = 0;
        int length = 0;
        List<String> coupons = null;
        while (0 == length && times < RETRYTIME) {
            Thread.sleep(2000);
            String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(COUPON+"?state=1")
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");
            coupons = JacksonUtil.parseSubnodeToStringList(ret, "/data/list");
            length = coupons.size();
            times++;
        }
        assertEquals(1, length);
        assertNotNull(coupons);
        assertEquals(2, JacksonUtil.parseSubnodeToObject(coupons.get(0), "/activityId", Integer.class));
        assertEquals(1, JacksonUtil.parseSubnodeToObject(coupons.get(0), "/state", Integer.class));
    }
    /**
     * 领每人限制优惠卷
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postCoupon6() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String ret = new String(Objects.requireNonNull(this.mallClient.post().uri(POSTCOUPON, 3)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(2)
                .returnResult().getResponseBody()),"UTF-8");
    }
    /**
     * 再领每人限制优惠卷
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postCoupon7() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.post().uri(POSTCOUPON, 3)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.COUPON_EXIST.getCode());
    }
    /**
     * 查询postCoupon5这张优惠卷
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getCoupon2() throws Exception {

        String token = this.customerLogin("699275", "123456");
        int times = 0;
        int length = 1;
        List<String> coupons = null;
        while (1 == length && times < RETRYTIME) {

            String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(COUPON+"?state=1")
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");
            coupons = JacksonUtil.parseSubnodeToStringList(ret, "/data/list");
            length = coupons.size();
            times++;
        }
        assertEquals(3, length);
        assertNotNull(coupons);
    }


}
