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

package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.privilege.vo.RetGroup;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
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
public class DepartsGroupsTest extends BaseTestOomall {

    private static final String GROUPURL = "/privilege/departs/{did}/groups";
    private static final String IDURL = "/privilege/departs/{did}/groups/{id}";

    private static Long platformGroupId = null;
    private static Long depart1GroupId = null;
    private static Long depart2GroupId = null;

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void postGroupTest1() throws Exception{

        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";

        this.gatewayClient.post().uri(GROUPURL,1)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 25
     * 不同部门管理员增加组
     */
    @Test
    public void postGroupTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.gatewayClient.post().uri(GROUPURL, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员新增角色
     */
    @Test
    @Order(1)
    public void postGroupTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GROUPURL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("hello")
                .jsonPath("$.data.descr").isEqualTo("管理员test")
                .returnResult().getResponseBody()),"UTF-8");

        RetGroup vo = JacksonUtil.parseObject(ret, "data", RetGroup.class);
        this.platformGroupId = vo.getId();

        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == 'hello')].descr").isEqualTo("管理员test");
    }

    /**
     * 24
     * 平台管理员新增部门1角色
     * @author Li Zihan
     */
    @Test
    @Order(2)
    public void postGroupTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test1\",\"name\": \"hello1\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GROUPURL, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        RetGroup vo = JacksonUtil.parseObject(ret, "data", RetGroup.class);
        this.depart1GroupId = vo.getId();

        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '管理员test1')].name").isEqualTo("hello1");
    }

    /**
     * 24
     * 部门管理员新增部门2角色
     */
    @Test
    @Order(3)
    public void postGroupTest5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test2\",\"name\": \"hello2\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GROUPURL, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        this.depart2GroupId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);

        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '管理员test2')].name").isEqualTo("hello2");

    }

    /**
     * 25
     * 未登录新增组
     */
    @Test
    public void postGroupTest6() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        this.gatewayClient.post().uri(GROUPURL, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 25
     * 未登录查询组
     */
    @Test
    public void getGroupTest1() throws Exception {
        this.gatewayClient.get().uri(GROUPURL, 2)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 25
     * 不同店铺管理员查询组
     */
    @Test
    public void getGroupTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(GROUPURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 25
     * 无权限
     */
    @Test
    public void getGroupTest3() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.get().uri(GROUPURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void putGroupTest1() throws Exception{

        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"descr\": \"修改\"}";

        this.gatewayClient.put().uri(IDURL,1, 6)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 25
     * 不同部门管理员增加组
     */
    @Test
    public void putGroupTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.gatewayClient.put().uri(IDURL, 1,6)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员修改组
     */
    @Test
    @Order(4)
    public void putGroupTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.platformGroupId);
        String json = "{\"descr\": \"修改\"}";
        this.gatewayClient.put().uri(IDURL, 0, this.platformGroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("hello")
                .jsonPath("$.data.descr").isEqualTo("修改");


        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '修改')].name").isEqualTo("hello");

    }

    /**
     * 24
     * 平台管理员修改部门1组
     */
    @Test
    @Order(5)
    public void putGroupTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.depart1GroupId);
        String roleJson = "{\"descr\": \"1111\"}";
        this.gatewayClient.put().uri(IDURL, 1, this.depart1GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("hello1")
                .jsonPath("$.data.descr").isEqualTo("1111");

        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '1111')].name").isEqualTo("hello1");
    }

    /**
     * 24
     * 部门管理员修改部门2角色
     */
    @Test
    @Order(6)
    public void putGroupTest5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        assertNotNull(this.depart2GroupId);
        String roleJson = "{\"descr\": \"2222\"}";
        this.gatewayClient.put().uri(IDURL, 2, this.depart2GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("hello2")
                .jsonPath("$.data.descr").isEqualTo("2222");


        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '2222')].name").isEqualTo("hello2");
    }

    /**
     * 25
     * 未登录修改组
     */
    @Test
    public void putGroupTest6() throws Exception {
        this.gatewayClient.put().uri(IDURL, 2, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void delGroupTest1() throws Exception{

        String token = this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.delete().uri(IDURL,1, 6)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 25
     * 不同部门管理员删除组
     */
    @Test
    public void delGroupTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL, 1,6)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员修改组
     */
    @Test
    @Order(7)
    public void delGroupTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.platformGroupId);
        this.gatewayClient.delete().uri(IDURL, 0, this.platformGroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '修改')]").doesNotExist();
    }

    /**
     * 24
     * 平台管理员修改部门1组
     */
    @Test
    @Order(8)
    public void delGroupTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.depart1GroupId);
        this.gatewayClient.delete().uri(IDURL, 1, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '1111')]").doesNotExist();
    }

    /**
     * 24
     * 部门管理员修改部门2角色
     */
    @Test
    @Order(9)
    public void delGroupTest5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        assertNotNull(this.depart2GroupId);
        this.gatewayClient.delete().uri(IDURL, 2, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        this.gatewayClient.get().uri(GROUPURL+"?page=1&pageSize=50", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.descr == '2222')]").doesNotExist();
    }

    /**
     * 25
     * 未登录删除组
     */
    @Test
    public void delGroupTest6() throws Exception {
        this.gatewayClient.delete().uri(IDURL, 2, 1)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
}
