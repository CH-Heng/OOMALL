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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentTest extends BaseTestOomall {

    private static final String STATES = "/comment/comments/states";
    private static final String PRODCOMMENT = "/comment/products/{id}/comments";
    private static final String CONFIRM = "/comment/shops/{did}/comments/{id}/confirm";
    private static final String COMMENT = "/comment/comments";
    private static final String NEWCOMMENT = "/comment/shops/{id}/newcomments";
    private static final String SHOPCOMMENT = "/comment/shops/{id}/comments";

    /**
     * 获得comment的所有状态
     * @throws Exception
     */
    @Test
    public void getCommentState() throws Exception {
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
     * id不存在
     * @throws Exception
     */
    @Test
    public void getPordComment1() throws Exception {
        this.mallClient.get().uri(PRODCOMMENT, 423893)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 获得comment的所有状态
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getPordComment2() throws Exception {
        this.mallClient.get().uri(PRODCOMMENT, 4893)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].type").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].content").isEqualTo("一般")
                .jsonPath("$.data.list[?(@.id == '3')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].post.id").isEqualTo(24654);
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    public void getNewComment1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(NEWCOMMENT, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getNewComment2() throws Exception {
        String token = this.adminLogin("comment", "123456");
        this.gatewayClient.get().uri(NEWCOMMENT, 0)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '1')].content").isEqualTo("真不错")
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '1')].post.id").isEqualTo(24653)
                .jsonPath("$.data.list[?(@.id == '2')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '2')].content").isEqualTo("真不错")
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '2')].post.id").isEqualTo(24653);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void confirmComment1() throws Exception {
        String json = "{\"conclusion\": true}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(CONFIRM, 0, 423893)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    public void confirmComment2() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(CONFIRM, 0, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * comment用户审核通过
     * @throws Exception
     */
    @Test
    @Order(2)
    public void confirmComment3() throws Exception {
        String token = this.adminLogin("comment", "123456");
        String json = "{\"conclusion\": true}";
        this.gatewayClient.put().uri(CONFIRM, 0, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(PRODCOMMENT, 4893)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '1')].content").isEqualTo("真不错")
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '1')].post.id").isEqualTo(24653);
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getNewComment3() throws Exception {
        String token = this.adminLogin("comment", "123456");
        this.gatewayClient.get().uri(NEWCOMMENT, 0)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '2')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '2')].content").isEqualTo("真不错")
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '2')].post.id").isEqualTo(24653);
    }
    /**
     * comment用户审核不通过
     * @throws Exception
     */
    @Test
    @Order(4)
    public void confirmComment4() throws Exception {
        String token = this.adminLogin("comment", "123456");
        String json = "{\"conclusion\": false}";
        this.gatewayClient.put().uri(CONFIRM, 0, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(PRODCOMMENT, 4893)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getNewComment4() throws Exception {
        String token = this.adminLogin("comment", "123456");
        this.gatewayClient.get().uri(NEWCOMMENT, 0)
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
     * customer1
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getComment1() throws Exception {
        String token = this.customerLogin("customer1", "123456");
        this.mallClient.get().uri(COMMENT)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '1')].post.id").isEqualTo(24653)
                .jsonPath("$.data.list[?(@.id == '1')].audit.id").isEqualTo(57)
                .jsonPath("$.data.list[?(@.id == '2')].type").isEqualTo(0)
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '2')].audit.id").isEqualTo(57)
                .jsonPath("$.data.list[?(@.id == '2')].post.id").isEqualTo(24653);
    }
    /**
     * customer2
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getComment2() throws Exception {
        String token = this.customerLogin("customer2", "123456");
        this.mallClient.get().uri(COMMENT)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].type").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '3')].post.id").isEqualTo(24654)
                .jsonPath("$.data.list[?(@.id == '3')].audit.id").isEqualTo(1);
    }
    /**
     * comment管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getShopComment1() throws Exception {
        String token = this.adminLogin("comment", "123456");
        this.gatewayClient.get().uri(SHOPCOMMENT,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getShopComment2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(SHOPCOMMENT,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1);
    }
}
