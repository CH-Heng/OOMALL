package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
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

/**
 * @author Ming Qiu
 * @date Created in 2020/12/18 22:29
 **/
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsRolesTest extends BaseTestOomall {

    private static final String GETURL="/privilege/departs/{did}/roles";

    private static final String IDURL="/privilege/departs/{did}/roles/{id}";

    private static final String FORBIDURL="/privilege/departs/{did}/roles/{id}/forbid";

    private static final String RELEASEURL="/privilege/departs/{did}/roles/{id}/release";

    private static final String GETURLDEPARTURL = "/privilege/departs/{did}/groups";

    private static Long depart1RoleId = null;
    private static Long depart2RoleId = null;
    private static Long platformRoleId = null;

    /**
     * 未登录查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest1() throws Exception {
        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=2",0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 使用伪造token查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest2() throws Exception {
        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=2",0)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 平台管理员查询店铺角色信息
     *
     */
    @Test
    @Order(1)
    public void selectRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '11')]").exists();
    }

    /**
     * 店铺管理员查询店铺角色信息
     *
     */
    @Test
    @Order(1)
    public void selectRoleTest4() throws Exception {
        String token = this.adminLogin("5961900008", "123456");
        this.gatewayClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '11')]").exists();
    }

    /**
     * 不同店铺管理员查询店铺角色信息
     *
     */
    @Test
    public void selectRoleTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(GETURL,10)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 无权限店铺管理员查询店铺角色信息
     */
    @Test
    public void selectRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.get().uri(GETURL,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 25
     * 不同部门管理员增加角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.gatewayClient.post().uri(GETURL, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员新增角色
     * @author Li Zihan
     */
    @Test
    @Order(3)
    public void insertRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"管理员\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        this.platformRoleId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.platformRoleId+"')].name").isEqualTo("管理员")
                .jsonPath("$.data.list[?(@.id == '"+this.platformRoleId+"')].descr").isEqualTo("管理员test");
    }

    /**
     * 24
     * 平台管理员新增部门1角色
     * @author Li Zihan
     */
    @Test
    @Order(4)
    public void insertRoleTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL, 1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");
        this.depart1RoleId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);
        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.depart1RoleId+"')].name").isEqualTo("hello")
                .jsonPath("$.data.list[?(@.id == '"+this.depart1RoleId+"')].descr").isEqualTo("管理员test");
    }

    /**
     * 24
     * 部门管理员新增部门2角色
     * @author Li Zihan
     */
    @Test
    @Order(5)
    public void insertRoleTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello2\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        this.depart2RoleId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.depart2RoleId+"')].name").isEqualTo("hello2")
                .jsonPath("$.data.list[?(@.id == '"+this.depart2RoleId+"')].descr").isEqualTo("管理员test");
    }

    /**
     * 25
     * 未登录新增角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest5() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        this.gatewayClient.post().uri(GETURL, 0)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 24
     * 无权限管理员新增部门2角色
     */
    @Test
    public void insertRoleTest6() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"hello\"}";
        this.gatewayClient.post().uri(GETURL, 2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 平台管理员修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(6)
    public void updateRoleTest1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"testU\"}";
        assertNotNull(this.depart1RoleId);
        this.gatewayClient.put().uri(IDURL, 1, this.depart1RoleId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.depart1RoleId+"')].name").isEqualTo("testU")
                .jsonPath("$.data.list[?(@.id == '"+this.depart1RoleId+"')].descr").isEqualTo("testU");
    }

    /**
     * 平台管理员修改角色角色名为空
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"\"}";
        this.gatewayClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * 平台管理员修改角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest4() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.gatewayClient.put().uri(IDURL, 11, 108)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 平台管理员修改角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest5() throws Exception {
        String token = adminLogin("2721900002", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.gatewayClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 未登录修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest6() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.gatewayClient.put().uri(IDURL, 1, 108)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 伪造token修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest7() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.gatewayClient.put().uri(IDURL, 1, 108)
                .header("authorization", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 无权限修改角色
     *
     */
    @Test
    public void updateRoleTest8() throws Exception {
        String token = adminLogin("shop1_coupon", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        this.gatewayClient.put().uri(IDURL, 1, 108)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 部门管理员修改角色
     *
     */
    @Test
    @Order(7)
    public void updateRoleTest9() throws Exception {
        String token = adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"testU\"}";
        assertNotNull(this.depart2RoleId);
        this.gatewayClient.put().uri(IDURL, 2, this.depart2RoleId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '"+this.depart2RoleId+"')].name").isEqualTo("testU")
                .jsonPath("$.data.list[?(@.id == '"+this.depart2RoleId+"')].descr").isEqualTo("testU");
    }
    /**
     * 25
     * 不同部门管理员禁止角色
     * Li Zihan
     */
    @Test
    public void forbidRoleTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 1,2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员禁止角色
     * @author Li Zihan
     */
    @Test
    @Order(8)
    public void forbidRoleTest2() throws Exception {

        //禁止前 有部门管理权限
        String token1 = this.adminLogin("delrole_user11", "123456");
        this.gatewayClient.get().uri(GETURLDEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 1,109)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //禁止后
        this.gatewayClient.get().uri(GETURLDEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 23
     * 部门管理员禁止角色
     * @author Li Zihan
     */
    @Test
    @Order(9)
    public void forbidRoleTest3() throws Exception {

        //禁止前 有部门管理权限
        String token1 = this.adminLogin("delrole_user22", "123456");
        this.gatewayClient.get().uri(GETURLDEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 2,110)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //禁止后
        this.gatewayClient.get().uri(GETURLDEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 24
     * 禁止不存在的角色
     */
    @Test
    public void forbidRoleTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 2,98635)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 24
     * 禁止已禁止的角色
     */
    @Test
    @Order(10)
    public void forbidRoleTest5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 2,110)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }

    /**
     * 25
     * 未登录禁止
     * Li Zihan
     */
    @Test
    public void forbidRoleTest6() throws Exception {

        this.gatewayClient.put().uri(FORBIDURL, 2,110)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 24
     * 无权限管理员
     */
    @Test
    public void forbidRoleTest7() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(FORBIDURL, 2,3)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 25
     * 不同部门管理员解禁角色
     * Li Zihan
     */
    @Test
    public void releaseRoleTest1() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 1,2)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 23
     * 平台管理员解禁角色
     */
    @Test
    @Order(11)
    public void releaseRoleTest2() throws Exception {

        //解禁前 无部门管理权限
        String token1 = this.adminLogin("delrole_user11", "123456");
        this.gatewayClient.get().uri(GETURLDEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 1,109)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //解禁后
        this.gatewayClient.get().uri(GETURLDEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 23
     * 部门管理员解禁角色
     */
    @Test
    @Order(12)
    public void releaseRoleTest3() throws Exception {

        //解禁前 无部门管理权限
        String token1 = this.adminLogin("delrole_user22", "123456");
        this.gatewayClient.get().uri(GETURLDEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2,110)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //解禁后
        this.gatewayClient.get().uri(GETURLDEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 24
     * 解禁不存在的角色
     */
    @Test
    public void releaseRoleTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2,98635)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 24
     * 解禁已解禁的角色
     */
    @Test
    @Order(13)
    public void releaseRoleTest5() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2,110)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }

    /**
     * 25
     * 未登录解禁
     */
    @Test
    public void releaseRoleTest6() throws Exception {

        this.gatewayClient.put().uri(RELEASEURL, 2,110)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 24
     * 无权限管理员
     */
    @Test
    public void releaseRoleTest7() throws Exception {
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.put().uri(RELEASEURL, 2,3)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 管理员删除角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL, 1, 1009)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 管理员删除角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 未登录删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest4() throws Exception {
        this.gatewayClient.delete().uri(IDURL, 1, 109)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());    }

    /**
     * 伪造token删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest5() throws Exception {
        this.gatewayClient.delete().uri(IDURL, 1, 109)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 平台管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(14)
    public void deleteRoleTest6() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user11", "123456");
        this.gatewayClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL, 1, 109)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100",1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '109')]").doesNotExist();

        this.gatewayClient.get().uri(GETURL,1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 同店铺管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(15)
    public void deleteRoleTest7() throws Exception {
        //判断用户有权限
        String token1 = this.adminLogin("delrole_user22", "123456");
        this.gatewayClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL, 2, 110)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(GETURL+"?page=1&pageSize=100",2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '110')]").doesNotExist();

        this.gatewayClient.get().uri(GETURL,2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

}
