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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 用户修改自己信息测试类
 *
 * @author 24320182203175 陈晓如
 * createdBy 陈晓如 2020/11/30 13:42
 * modifiedBy Ming Qiu 2021/12/14 13:42
 **/
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SelfTest extends BaseTestOomall {

    private static final String USERURL = "/privilege/self/users";

    private static final String ROLEURL = "/privilege/self/roles";

    private static final String BROLEURL = "/privilege/self/baseroles";

    private static final String IMGURL = "/privilege/self/users/uploadImg";

    private static final String GROUPURL = "/privilege/self/groups";

    private static final String PASSURL = "/privilege/self/password";

    private static final  String RESETURL = "/privilege/self/password/reset";

    private static final String SELFPROXYURL = "/privilege/self/proxies";

    private static final String SELFIDURL = "/privilege/self/proxies/{id}";

    private static final String DEPARTURL ="/privilege/departs/{id}/proxies";

    private static final String PROXYURL ="/privilege/users/{id}/proxies";

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(ROLEURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '1')]").exists();
    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest2() throws Exception {
        String token = this.adminLogin("8532600003", "123456");
        this.gatewayClient.get().uri(ROLEURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4')]").exists();
    }


    /**
     * 修改自己的信息测试2: 管理员未登录修改自己的信息
     *
     * @author 24320182203175 陈晓如
     * createdBy 陈晓如 2020/12/01 10:43
     * modifiedBy 陈晓如 2020/12/01 10:43
     */
    @Test
    public void changeMyAdminselfInfo1() throws Exception {
        String userJson = "{\"name\": \"oomall\"," +
                "\"idNumber\": \"123456789\"," +
                "\"passportNumber\": \"12345678\"}";
        this.gatewayClient.put().uri(USERURL)
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 正常修改
     *
     * @throws Exception
     */
    @Test
    public void changeMyAdminselfInfo2() throws Exception {
        String token = this.adminLogin("change_user", "123456");
        String userJson = "{\"name\": \"oomall\"," +
                "\"idNumber\": \"123456789\"," +
                "\"passportNumber\": \"12345678\"}";
        this.gatewayClient.put().uri(USERURL).
                header("authorization", token).
                bodyValue(userJson).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(USERURL).
                header("authorization", token).
                exchange().
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("oomall")
                .jsonPath("$.data.idNumber").isEqualTo("123456789")
                .jsonPath("$.data.passportNumber").isEqualTo("12345678");
    }

    /**
     * 查询自己的BaseRole
     * @throws Exception
     */
    @Test
    public void getSelfBaseRoleTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(BROLEURL+"?page=1&pageSize=100")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '88')]").exists()
                .jsonPath("$.data.list[?(@.id == '89')]").exists()
                .jsonPath("$.data.list[?(@.id == '90')]").exists()
                .jsonPath("$.data.list[?(@.id == '91')]").exists()
                .jsonPath("$.data.list[?(@.id == '92')]").exists();
    }

    /**
     * 查看自己的用户测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserTest1() throws Exception {
        String token = this.adminLogin("8532600003", "123456");
        this.gatewayClient.get().uri(USERURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(48);
    }

    /**
     * 重置密码-与原密码相同
     * @throws Exception
     */
    @Test
    @Order(1)
    public void resetPassTest1() throws Exception {
        String json = "{\"name\":\"changepass\"}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        json = String.format("{\"name\":\"changepass\", \"captcha\": \"%s\", \"newPassword\": \"123456\"}", captcha);
        this.gatewayClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.PASSWORD_SAME.getCode());

    }

    /**
     * 重置密码-用户名不存在
     * @throws Exception
     */
    @Test
    public void resetPassTest2() throws Exception {
        Map<String, String> name = new HashMap<>();
        name.put("name","changepassnoexist");
        String json = JacksonUtil.toJson(name);

        this.gatewayClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_ID_NOTEXIST.getCode());
    }

    /**
     * 重置密码- 用邮箱
     * @throws Exception
     */
    @Test
    @Order(3)
    public void resetPassTest6() throws Exception {

        String requireJson = String.format(ADMINTEMP, "2235d@1245f", "123456");
        gatewayClient.post().uri("/privilege/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        Map<String, String> name = new HashMap<>();
        name.put("name","2235d@1245f");
        String json = JacksonUtil.toJson(name);
        String ret = new String(Objects.requireNonNull(this.gatewayClient.put().uri(RESETURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseString(ret, "data");
        name.put("captcha", captcha);
        name.put("newPassword", "223344");
        json = JacksonUtil.toJson(name);
        this.gatewayClient.put().uri(PASSURL)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        requireJson = String.format(ADMINTEMP, "changepass", "223344");
        gatewayClient.post().uri("/privilege/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 1
     * 不登录查询自己用户代理关系
     *
     */
    @Test
    public void getSelfProxies1() throws Exception {

        this.gatewayClient.get().uri(SELFPROXYURL)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 查询自己的代理关系
     *
     */
    @Test
    @Order(1)
    public void getSelfProxies2() throws Exception {

        String token = this.adminLogin("shop1_proxy", "123456");
        this.gatewayClient.get().uri(SELFPROXYURL)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17332)].user.id").isEqualTo(17337);
    }

    /**
     * 7
     * 伪造token查询代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getSelfProxies3() throws Exception {
        this.gatewayClient.get().uri(SELFPROXYURL)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 1
     * 不登录删除自己用户代理关系
     *
     */
    @Test
    public void delSelfProxies1() throws Exception {

        this.gatewayClient.delete().uri(SELFIDURL, 6)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 删除不是自己的用户代理关系
     *
     */
    @Test
    public void delSelfProxies2() throws Exception {
        String token = this.adminLogin("proxy_user1", "123456");
        this.gatewayClient.delete().uri(SELFIDURL, 6)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 2
     * 删除不是自己的用户代理关系, 及时时平台管理员也不行
     *
     */
    @Test
    public void delSelfProxies3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(SELFIDURL, 6)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 7
     * 伪造token删除代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void delSelfProxies4() throws Exception {
        this.gatewayClient.delete().uri(SELFIDURL, 6)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserGroupTest1() throws Exception {
        String token = this.adminLogin("shop1_auth", "123456");

        this.gatewayClient.get().uri(GROUPURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4')]").exists();
    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserGroupTest2() throws Exception {
        String token = this.adminLogin("comment", "123456");
        this.gatewayClient.get().uri(GROUPURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '3')]").exists();
    }
}

