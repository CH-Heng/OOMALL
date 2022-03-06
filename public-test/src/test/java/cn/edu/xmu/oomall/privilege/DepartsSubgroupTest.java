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

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsSubgroupTest extends BaseTestOomall {

    private static final String IDURL ="/privilege/departs/{did}/groups/{pid}/subgroups/{sid}";
    private static final String SUBGROUPURL ="/privilege//departs/{did}/groups/{id}/subgroups";
    private static final String PARENTURL ="/privilege//departs/{did}/groups/{id}/parents";
    private static final String GROUPURL = "/privilege/departs/{did}/groups";
    private static final String USERGROUPID = "/privilege/departs/{did}/groups/{id}/users/{uid}";
    private static final String GROUPUSER = "/privilege/departs/{did}/groups/{id}/users";
    private static final String USERGROUP = "/privilege/departs/{did}/users/{id}/groups";
    private static final String FORBIDURL = "/privilege/departs/{did}/groups/{id}/forbid";
    private static final String RELEASEURL = "/privilege/departs/{did}/groups/{id}/release";
    private static final String GROUPROLE = "/privilege/departs/{did}/groups/{id}/roles";
    private static final String GROUPBROLE = "/privilege/departs/{did}/groups/{id}/baseroles";
    private static final String USERIDURL = "/privilege/departs/{did}/users/{id}";


    private static Long depart1GroupId = null;
    private static Long depart2GroupId = null;
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void getSubgroupTest1() throws Exception {
        this.gatewayClient.get().uri(SUBGROUPURL,1, 4)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 伪造token
     * @throws Exception
     */
    @Test
    public void getSubgroupTest2() throws Exception {
        this.gatewayClient.get().uri(SUBGROUPURL,1, 4)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void getSubgroupTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.get().uri(SUBGROUPURL,1, 4)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getSubgroupTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(SUBGROUPURL,1, 4)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '8')]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getSubgroupTest6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(SUBGROUPURL,2, 5)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '9')]").exists();
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void getParentTest1() throws Exception {
        this.gatewayClient.get().uri(PARENTURL,1, 8)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 伪造token
     * @throws Exception
     */
    @Test
    public void getParentTest2() throws Exception {
        this.gatewayClient.get().uri(PARENTURL,1, 8)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void getParentTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 8)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getParentTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 8)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4')]").exists()
                .jsonPath("$.data.list[?(@.id == '6')]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getParentTest6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(PARENTURL,2, 9)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '5')]").exists()
                .jsonPath("$.data.list[?(@.id == '7')]").exists();
    }

    /**
     * 24
     * 平台管理员新增部门1
     */
    @Test
    @Order(2)
    public void postGroupTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"name\": \"subgroup1\"}";
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
                .jsonPath("$.data.list[?(@.name == 'subgroup1')]").exists();
    }

    /**
     * 24
     * 部门管理员新增部门2
     */
    @Test
    @Order(2)
    public void postGroupTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"name\": \"subgroup2\"}";
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
                .jsonPath("$.data.list[?(@.name == 'subgroup2')]").exists();
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void postGroupUserTest1() throws Exception {
        this.gatewayClient.post().uri(USERGROUPID,1, 4, 17360)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 伪造token
     * @throws Exception
     */
    @Test
    public void postGroupUserTest2() throws Exception {
        this.gatewayClient.post().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void postGroupUserTest3() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.post().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void postGroupUserTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.post().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void delGroupUserTest1() throws Exception {
        this.gatewayClient.delete().uri(USERGROUPID,1, 4, 17360)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 伪造token
     * @throws Exception
     */
    @Test
    public void delGroupUserTest2() throws Exception {
        this.gatewayClient.delete().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void delGroupUserTest3() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void delGroupUserTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postdelGroupUserTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        String token1 = this.adminLogin("group_user1", "123456");

        //增加前无权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        this.gatewayClient.post().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(17360);

        this.gatewayClient.get().uri(USERGROUP,1, 17360)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4')]").exists();

        //增加后有权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.delete().uri(USERGROUPID,1, 4, 17360)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //删除后无权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        assertNotNull(this.depart1GroupId);
        this.gatewayClient.post().uri(USERGROUPID,1, this.depart1GroupId, 17360)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(17360);

        this.gatewayClient.get().uri(GROUPUSER,1, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '17360')]").exists();

    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postdelGroupUserTest6() throws Exception {
        String token1 = this.adminLogin("group_user2", "123456");
        //增加前无权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(USERGROUPID,2, 5, 17361)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(17361);

        this.gatewayClient.get().uri(USERGROUP,2, 17361)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '5')]").exists();

        //增加后有权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.delete().uri(USERGROUPID,2, 5, 17361)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //删除后无权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        assertNotNull(this.depart2GroupId);
        this.gatewayClient.post().uri(USERGROUPID,2, this.depart2GroupId, 17361)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(17361);

        this.gatewayClient.get().uri(GROUPUSER,2, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '17361')]").exists();
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void postSubgroupTest1() throws Exception {
        this.gatewayClient.post().uri(IDURL,1, 4, 8)
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
    public void postSubgroupTest2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.post().uri(IDURL,1, 4, 8)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void postSubgroupTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.post().uri(IDURL,1, 4, 8)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postSubgroupTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.post().uri(IDURL,2, 5, 112348)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postSubgroupTest5() throws Exception {
        assertNotNull(this.depart1GroupId);

        //增加前用户无权限
        String token1 = this.adminLogin("group_user1", "123456");

        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(IDURL,1, 4, this.depart1GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart1GroupId);

        //增加后用户有权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postSubgroupTest6() throws Exception {

        assertNotNull(this.depart2GroupId);

        //增加前用户无权限
        String token1 = this.adminLogin("group_user2", "123456");
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(IDURL,2, 5, this.depart2GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart2GroupId);

        //增加后用户有权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void getGroupRoleTest1() throws Exception {
        this.gatewayClient.get().uri(GROUPROLE,1, 4)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void getGroupRoleTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(GROUPROLE,1, 4)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getGroupRoleTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(GROUPROLE,2, 10224)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getGroupRoleTest5() throws Exception {
        assertNotNull(this.depart1GroupId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(GROUPROLE,1, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '111')]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getGroupRoleTest6() throws Exception {

        assertNotNull(this.depart2GroupId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(GROUPROLE,2, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '112')]").exists();
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void getGroupBRoleTest1() throws Exception {
        this.gatewayClient.get().uri(GROUPBROLE,1, 4)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void getGroupBRoleTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(GROUPBROLE,1, 4)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getGroupBRoleTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(GROUPBROLE,2, 10224)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getGroupBRoleTest5() throws Exception {
        assertNotNull(this.depart1GroupId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(GROUPBROLE,1, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '97')]").exists()
                .jsonPath("$.data.list[?(@.id == '99')]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getGroupBRoleTest6() throws Exception {

        assertNotNull(this.depart2GroupId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(GROUPBROLE,2, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '97')]").exists()
                .jsonPath("$.data.list[?(@.id == '99')]").exists();
    }
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void forbidGroupTest1() throws Exception {
        this.gatewayClient.put().uri(FORBIDURL,1, 4)
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
    public void forbidGroupTest2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(FORBIDURL,1, 4)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void forbidGroupTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL,1, 4)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void forbidGroupTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL,2, 12321433)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void forbidGroupTest5() throws Exception {
        assertNotNull(this.depart1GroupId);
        String token1 = this.adminLogin("group_user1", "123456");
        //前有权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(FORBIDURL,1, this.depart1GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart1GroupId);
        //用户无权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void forbidGroupTest6() throws Exception {

        assertNotNull(this.depart2GroupId);
        //前有权限
        String token1 = this.adminLogin("group_user2", "123456");
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL,2, this.depart2GroupId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart2GroupId);

        //后无权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void releaseGroupTest1() throws Exception {
        this.gatewayClient.put().uri(RELEASEURL, 1,4)
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
    public void releaseGroupTest2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 1,4)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void releaseGroupTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 1,4)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void releaseGroupTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2,1223423)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(6)
    public void releaseGroupTest5() throws Exception {
        assertNotNull(this.depart1GroupId);
        //增加前用户无权限
        String token1 = this.adminLogin("group_user1", "123456");
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 1, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart1GroupId);

        //增加后用户有权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(6)
    public void releaseGroupTest6() throws Exception {

        assertNotNull(this.depart2GroupId);
        //增加前用户无权限
        String token1 = this.adminLogin("group_user2", "123456");
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //增加后用户有权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void delSubgroupTest1() throws Exception {
        this.gatewayClient.delete().uri(IDURL,1, 4, 8)
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
    public void delSubgroupTest2() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(IDURL,1, 4, 8)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 不同部门管理员
     * @throws Exception
     */
    @Test
    public void delSubgroupTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL,1, 4, 8)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delSubgroupTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL,2, 5, 999931)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(7)
    public void delSubgroupTest5() throws Exception {
        assertNotNull(this.depart1GroupId);


        String token1 = this.adminLogin("group_user1", "123456");
        //删除前用户有权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(IDURL,1, 4, this.depart1GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart1GroupId);

        //删除精辟用户无权限
        this.gatewayClient.get().uri(USERIDURL, 1, 46)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(7)
    public void delSubgroupTest6() throws Exception {

        assertNotNull(this.depart2GroupId);

        //删除前有权限
        String token1 = this.adminLogin("group_user2", "123456");
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL,2, 5, this.depart2GroupId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.depart2GroupId);

        //删除后无权限
        this.gatewayClient.get().uri(USERIDURL, 2, 47)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }


}
