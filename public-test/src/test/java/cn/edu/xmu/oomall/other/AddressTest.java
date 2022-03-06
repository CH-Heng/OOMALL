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
public class AddressTest extends BaseTestOomall {
    private static final String ADDRESS = "/customer/addresses";
    private static final String ID = "/customer/addresses/{id}";
    private static final String DEFAULT = "/customer/addresses/{id}/default";

    private static Integer addressId = null;

    @Test
    @Order(1)
    public void getAddress1() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.get().uri(ADDRESS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(4)
                .jsonPath("$.data[?(@.id == '10')].region.name").isEqualTo("珠山区")
                .jsonPath("$.data[?(@.id == '11')].region.name").isEqualTo("珠山区")
                .jsonPath("$.data[?(@.id == '12')].region.name").isEqualTo("珠山区")
                .jsonPath("$.data[?(@.id == '13')].region.name").isEqualTo("珠山区");
    }

    /**
     * 增加地址至最大值
     *
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postAddress1() throws Exception {
        String token = this.customerLogin("348671", "123456");
        String json = "{\"regionId\": 1604, \"detail\": \"新店镇翔安南路%d号\", \"consignee\": \"赵佛晓\", \"mobile\": \"13959286326\"}";

        for (int i = 5; i < 21; i++) {
            String ret = new String(Objects.requireNonNull(this.mallClient.post().uri(ADDRESS)
                    .header("authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(String.format(json, i))
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF-8");

            addressId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);
            Thread.sleep(100);

            this.mallClient.get().uri(ADDRESS)
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .jsonPath("$.data.length()").isEqualTo(i)
                    .jsonPath("$.data[?(@.id == '" + addressId + "')].region.name").isEqualTo("翔安区")
                    .jsonPath("$.data[?(@.id == '" + addressId + "')].detail").isEqualTo(String.format("新店镇翔安南路%d号", i))
                    .jsonPath("$.data[?(@.id == '" + addressId + "')].consignee").isEqualTo("赵佛晓")
                    .jsonPath("$.data[?(@.id == '" + addressId + "')].mobile").isEqualTo("13959286326")
                    .jsonPath("$.data[?(@.id == '" + addressId + "')].beDefault").isEqualTo(false);
        }
    }

    /**
     * 超过最大值
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postAddress2() throws Exception {
        String token = this.customerLogin("348671", "123456");
        String json = "{\"regionId\": 1604, \"detail\": \"新店镇翔安南路\", \"consignee\": \"赵佛晓\", \"mobile\": \"13959286326\"}";

        this.mallClient.post().uri(ADDRESS)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.ADDRESS_OUTLIMIT.getCode());
    }

    /**
     * 设置默认值
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putDefaultAddress1() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.put().uri(DEFAULT, 10)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(ADDRESS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.id == '10')].beDefault").isEqualTo(true);
    }
    /**
     * 设置默认值
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void putDefaultAddress2() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.put().uri(DEFAULT, 11)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(ADDRESS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.id == '10')].beDefault").isEqualTo(false)
                .jsonPath("$.data[?(@.id == '11')].beDefault").isEqualTo(true);
    }
    /**
     * id不存在
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void putDefaultAddress3() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.put().uri(DEFAULT, 111234365)
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
     * id不存在
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void delAddress1() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.delete().uri(ID, 111234365)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 设置默认值
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void delAddress2() throws Exception {
        String token = this.customerLogin("348671", "123456");
        this.mallClient.delete().uri(ID, 10)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(ADDRESS)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[?(@.id == '10')]").doesNotExist();
    }

}
