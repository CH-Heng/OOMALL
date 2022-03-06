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

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShareTest extends BaseTestOomall {
    private static final String POSTSHARE = "/share/onsales/{id}/shares";
    private static final String SHARE = "/share/shares";
    private static final String SHAREPROD = "/share/shares/{sid}/products/{id}";
    private static final String PRODSHARE = "/share/shops/{did}/products/{id}/shares";
    private static final String BESHARE = "/share/beshared";
    private static final String PRODBESHARE = "/share/shops/{did}/products/{id}/beshared";

    private static Integer shareId = null;

    @Test
    @Order(1)
    public void getShare1() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.get().uri(SHARE)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

    /**
     * 建立分享
     * 1558商品
     *
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postShare1() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String ret = new String(Objects.requireNonNull(this.mallClient.post().uri(POSTSHARE, 9)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        shareId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);

        this.mallClient.get().uri(SHARE)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '" + shareId + "')].sharer.id").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '" + shareId + "')].product.id").isEqualTo(1558)
                .jsonPath("$.data.list[?(@.id == '" + shareId + "')].product.name").isEqualTo("奥利奥树莓蓝莓")
                .jsonPath("$.data.list[?(@.id == '" + shareId + "')].quantity").isEqualTo(0);
    }

    /**
     * 管理员查询1558商品的分享
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getShopShare1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(PRODSHARE, 2, 1558)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .returnResult().getResponseBody()),"UTF-8");

        assertEquals(1, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/sharer/id", Integer.class));
        assertEquals(1558, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/product/id", Integer.class));
        assertEquals(0, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/quantity", Integer.class));
    }

        /**
         * 建立分享
         * 未建立分享活动
         *
         * @throws Exception
         */
    @Test
    public void postShare2() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.post().uri(POSTSHARE, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.SHARE_UNSHARABLE.getCode());
    }

    /**
     * 查询自己分享成功
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getBeShared1() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.get().uri(BESHARE)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

    /**
     * 分享成功
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getShareProd1() throws Exception {
        assertNotNull(shareId);
        String token = this.customerLogin("105048", "123456");
        this.mallClient.get().uri(SHAREPROD, shareId, 1558)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1558)
                .jsonPath("$.data.name").isEqualTo("奥利奥树莓蓝莓")
                .jsonPath("$.data.onsaleId").isEqualTo(9)
                .jsonPath("$.data.originalPrice").isEqualTo(65283)
                .jsonPath("$.data.price").isEqualTo(6985)
                .jsonPath("$.data.shareable").isEqualTo(true);

        String token1 = this.customerLogin("699275", "123456");
        String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(BESHARE)
                .header("authorization", token1)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .returnResult().getResponseBody()), "UTF-8");

        assertEquals(1558, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/product/id", Integer.class));
        assertEquals("奥利奥树莓蓝莓", JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/product/name", String.class));
        assertEquals(1, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/sharerId", Integer.class));
        assertEquals(2, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/customerId", Integer.class));
        assertEquals(0, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/state", Integer.class));
    }

    /**
     * 管理员查询分享成功
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getShopBeShare1() throws Exception {
        assertNotNull(shareId);
        String token = this.adminLogin("2721900002", "123456");

        String ret = new String(Objects.requireNonNull(this.gatewayClient.get().uri(PRODBESHARE, 2, 1558)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .returnResult().getResponseBody()), "UTF-8");

        assertEquals(1558, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/product/id", Integer.class));
        assertEquals("奥利奥树莓蓝莓", JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/product/name", String.class));
        assertEquals(1, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/sharerId", Integer.class));
        assertEquals(2, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/customerId", Integer.class));
        assertEquals(0, JacksonUtil.parseSubnodeToObject(ret, "/data/list/0/state", Integer.class));
    }
    /**
     * 不同店铺管理员
     *
     * @throws Exception
     */
    @Test
    public void getShopBeShare2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(PRODBESHARE, 2, 1558)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
}
