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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartTest extends BaseTestOomall {

    private static final String CARTS = "/customer/carts";

    /**
     * 增加的购物车
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postCarts1() throws Exception {
        String token = this.customerLogin("699275", "123456");
        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);

        String json = "{\"productId\": 1608, \"quantity\": 1}";
        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.price").isEqualTo(24800);

        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.product.id == '1608')].product.name").isEqualTo("老阿妈火锅料")
                .jsonPath("$.data.list[?(@.product.id == '1608')].price").isEqualTo(24800)
                .jsonPath("$.data.list[?(@.product.id == '1608')].quantity").isEqualTo(1);

        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.price").isEqualTo(24800);

        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.product.id == '1608')].product.name").isEqualTo("老阿妈火锅料")
                .jsonPath("$.data.list[?(@.product.id == '1608')].price").isEqualTo(24800)
                .jsonPath("$.data.list[?(@.product.id == '1608')].quantity").isEqualTo(2);
    }

    /**
     * 商品null
     *
     * @throws Exception
     */
    @Test
    public void postCarts2() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String json = "{\"productId\": null, \"quantity\": 1}";
        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }
    /**
     * quantity无
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postCarts3() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String json = "{\"productId\": 1608}";
        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.price").isEqualTo(24800);

        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.product.id == '1608')].product.name").isEqualTo("老阿妈火锅料")
                .jsonPath("$.data.list[?(@.product.id == '1608')].price").isEqualTo(24800)
                .jsonPath("$.data.list[?(@.product.id == '1608')].quantity").isEqualTo(3);
    }
    /**
     * quantity负数
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postCarts4() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String json = "{\"productId\": 1608, \"quantity\": -1}";
        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.price").isEqualTo(24800);

        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.product.id == '1608')].product.name").isEqualTo("老阿妈火锅料")
                .jsonPath("$.data.list[?(@.product.id == '1608')].price").isEqualTo(24800)
                .jsonPath("$.data.list[?(@.product.id == '1608')].quantity").isEqualTo(2);
    }
    /**
     * quantity大负数
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postCarts5() throws Exception {
        String token = this.customerLogin("699275", "123456");
        String json = "{\"productId\": 1608, \"quantity\": -10}";
        this.mallClient.post().uri(CARTS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(CARTS)
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
     * 获得的购物车
     *
     * @throws Exception
     */
    @Test
    public void deleteCarts1() throws Exception {
        String token = this.customerLogin("974060", "123456");
        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.product.id == '4280')].product.name").isEqualTo("立白强效去渍洗衣粉")
                .jsonPath("$.data.list[?(@.product.id == '4450')].product.name").isEqualTo("旺旺O泡草莓味");

        this.mallClient.delete().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk();

        this.mallClient.get().uri(CARTS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

}