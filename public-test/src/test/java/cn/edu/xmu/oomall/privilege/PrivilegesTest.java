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
import cn.edu.xmu.oomall.privilege.vo.PrivilegeRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrivilegesTest extends BaseTestOomall {

    private static final String TESTURL = "/privilege/departs/{did}/privileges";
    private static final String IDURL = "/privilege/departs/{did}/privileges/{id}";
    private static final String FORBIDURL = "/privilege/departs/{did}/privileges/{id}/forbid";
    private static final String RELEASEURL = "/privilege/departs/{did}/privileges/{id}/release";

    private static final String USERURL = "/privilege/departs/{did}/users/{id}";

    private static Long privId = null;

    /**
     * 获取所有权限（第一页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL + "?page=1&pageSize=11", 0).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list.length()").isEqualTo(11)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 获取所有权限（第一页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL + "?requestType=3&page=1&pageSize=100", 0).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list[?(@.id == '4')].name").isEqualTo("删除用户")
                .jsonPath("$.data.list[?(@.id == '4')].requestType").isEqualTo(3);
    }

    /**
     * 未登录
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv3() throws Exception {
        this.gatewayClient.get().uri(TESTURL, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 获取所有权限（第二页）
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL + "?page=2&pageSize=14", 0).
                header("authorization", token).
                exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(14)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 非平台管理员
     *
     * @throws Exception
     */
    @Test
    public void getAllPriv5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(TESTURL + "?page=2&pageSize=10", 0).header("authorization", token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 未登录
     *
     * @throws Exception
     */
    @Test
    public void addPrivTest1() throws Exception {
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        this.gatewayClient.post().uri(TESTURL,0)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 非平台管理员
     *
     * @throws Exception
     */
    @Test
    public void addPrivTest2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        this.gatewayClient.post().uri(TESTURL,0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * URL和Type重复
     */
    @Test
    public void addPrivTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 0}";
        this.gatewayClient.post().uri(TESTURL,0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.URL_SAME.getCode());
    }

    /**
     * 权限增加成功
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void addPrivTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(TESTURL,0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent()), "UTF-8");

        this.privId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);
    }

    /**
     * 修改成功
     * @throws Exception
     */
    @Test
    @Order(2)
    public void changePrivTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.privId);

        String privJson = "{\"name\":\"测试改变1\", \"url\": \"/users/{id}/testChange\"}";
        this.gatewayClient.put().uri(IDURL, 0, this.privId).header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 2
     * 修改权限，非平台管理员
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String privJson = "{\"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 2}";
        this.gatewayClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 5
     * 修改权限，requestType数值错误
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"7\"}";
        this.gatewayClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * 6
     * 修改权限，输入了重复的url和requestType
     *
     * @throws Exception
     * @author 张湘君 24320182203327
     */
    @Test
    public void changePrivTest6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String privJson = "{\"url\": \"/privilege/departs/{id}/users/{id}\", \"requestType\": 2}";
        this.gatewayClient.put().uri(IDURL,0,2)
                .header("authorization", token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.URL_SAME.getCode());
    }

    /**
     * 删除成功
     * @throws Exception
     */
    @Test
    @Order(3)
    public void delPrivTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        assertNotNull(this.privId);

        this.gatewayClient.delete().uri(IDURL, 0, this.privId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.delete().uri(IDURL, 0, this.privId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

    }

    /**
     * 2
     * 修改权限，非平台管理员
     *
     * @throws Exception
     */
    @Test
    public void delPrivTest2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(IDURL,0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 25
     * 非平台管理员禁止权限
     */
    @Test
    public void forbidPrivilegeTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }


    /**
     * 24
     * 禁止不存在的角色
     */
    @Test
    public void forbidPrivilegeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 0,98635)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }


    /**
     * 25
     * 未登录禁止
     * Li Zihan
     */
    @Test
    public void forbidPrivilegeTest3() throws Exception {

        this.gatewayClient.put().uri(FORBIDURL, 0,2)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 23
     * 平台管理员禁止权限
     */
    @Test
    @Order(4)
    public void forbidPrivilegeTest4() throws Exception {

        //禁止前 有部门管理权限
        String token1 = this.adminLogin("shop1_auth", "123456");
        this.gatewayClient.get().uri(USERURL, 1, 17332)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //禁止后
        this.gatewayClient.get().uri(USERURL, 1, 17332)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }


    /**
     * 24
     * 禁止已禁止的角色
     */
    @Test
    @Order(5)
    public void forbidPrivilegeTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }

    /**
     * 25
     * 非平台管理员解禁权限
     */
    @Test
    public void releasePrivilegeTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }


    /**
     * 24
     * 解禁不存在的角色
     */
    @Test
    public void releasePrivilegeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 0,98635)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }


    /**
     * 25
     * 未登录解禁
     * Li Zihan
     */
    @Test
    public void releasePrivilegeTest3() throws Exception {

        this.gatewayClient.put().uri(RELEASEURL, 0,2)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 23
     * 平台管理员解禁权限
     */
    @Test
    @Order(6)
    public void releasePrivilegeTest4() throws Exception {

        //解禁前 无部门管理权限
        String token1 = this.adminLogin("shop1_auth", "123456");
        this.gatewayClient.get().uri(USERURL, 1, 17332)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 0,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //解禁后
        this.gatewayClient.get().uri(USERURL, 1, 17332)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }
    /**
     * 24
     * 解禁已解禁的权限
     */
    @Test
    @Order(7)
    public void releasePrivilegeTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 0, 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }
}
