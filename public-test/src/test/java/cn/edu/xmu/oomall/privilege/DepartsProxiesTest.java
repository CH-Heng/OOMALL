package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.privilege.vo.UserProxyRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Li Zihan 24320182203227
 * @date Created in 2020/12/9 12:33
 **/
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsProxiesTest extends BaseTestOomall {

    private static final String DEPARTURL ="/privilege/departs/{id}/proxies";
    private static final String DEPARTIDURL = "/privilege/departs/{did}/proxies/{id}";
    private static final String PROXYURL ="/privilege/users/{id}/proxies";
    private static final String IDURL ="/privilege/departs/{did}/users/{aid}/proxyusers/{bid}";
    private static final String SELFPROXYURL = "/privilege/self/proxies";
    private static final String SELFIDURL = "/privilege/self/proxies/{id}";


    /**
     * 1
     * 不登录查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies1() throws Exception {

        this.gatewayClient.get().uri(DEPARTURL,1)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
    * 2
    * 平台管理员查询任意部门用户代理关系
    *
    * @author 24320182203227 Li Zihan
    */
    @Test
    @Order(1)
    public void getListProxies2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == '17332')].user.id").isEqualTo(17337);
    }

    /**
     * 3
     * 店铺管理员查询自己部门所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    @Order(1)
    public void getListProxies3() throws Exception {

        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == '17332')].user.id").isEqualTo(17337);
    }

    /**
     * 4
     * 管理员查询非自己部门用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies4() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(DEPARTURL,2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 5
     * 无权限管理员查询用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies5() throws Exception {

        String token = this.adminLogin("norole_user1", "123456");
        this.gatewayClient.get().uri(DEPARTURL,1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 7
     * 伪造token查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies7() throws Exception {
        this.gatewayClient.get().uri(DEPARTURL,0)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }


    /**
     * 1
     * 不登录创建代理关系
     *
     */
    @Test
    public void createProxies1() throws Exception {
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(PROXYURL,17330)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 1
     * 代理不同店的用户
     *
     */
    @Test
    public void createProxies2() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(PROXYURL,17333)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理平台管理员
     *
     */
    @Test
    public void createProxies3() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(PROXYURL,1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理成功
     *
     */
    @Test
    @Order(2)
    public void createProxies4() throws Exception {

        String token = this.adminLogin("proxy_user1", "123456");
        //代理前无权限
        this.gatewayClient.get().uri(DEPARTURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token1 = this.adminLogin("shop1_auth", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(PROXYURL, 17351)
                .header("authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //代理后权限
        this.gatewayClient.get().uri(DEPARTURL, 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 1
     * 不登录创建代理关系
     *
     */
    @Test
    public void createDepartsProxies1() throws Exception {
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(IDURL,1,17351,17330)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 1
     * 代理不同店的用户
     *
     */
    @Test
    public void createDepartsProxies2() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(IDURL,2,17352,17333)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 代理平台管理员
     *
     */
    @Test
    public void createDepartsProxies3() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(IDURL,1,17351,1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 1
     * 无权限
     *
     */
    @Test
    public void createDepartsProxies4() throws Exception {

        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(IDURL,1,17351,1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 1
     * 代理成功
     *
     */
    @Test
    @Order(3)
    public void createDepartProxies5() throws Exception {

        String token1 = this.adminLogin("proxy_user2", "123456");
        //代理前无权限
        this.gatewayClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

        String token = this.adminLogin("shop2_auth", "123456");
        String json = "{\"beginDate\": \"2021-12-10T20:38:20.000+08:00\",\"endDate\": \"2022-12-10T20:38:20.000+08:00\"}";
        this.gatewayClient.post().uri(IDURL,2,17352,17333)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //代理后权限
        this.gatewayClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }


    /**
     * 1
     * 不登录删除自己用户代理关系
     *
     */
    @Test
    public void delProxies1() throws Exception {

        this.gatewayClient.delete().uri(DEPARTIDURL, 2,7)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 2
     * 删除不是同部门的用户代理关系
     *
     */
    @Test
    public void delProxies2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(DEPARTIDURL, 2,7)
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
    public void delProxies4() throws Exception {
        this.gatewayClient.delete().uri(DEPARTIDURL, 2,7)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }


    /**
     * 2
     * 删除同部门的用户代理关系
     * 在createDepartProxies5之后运行
     */
    @Test
    @Order(4)
    public void delProxies5() throws Exception {

        String token = this.adminLogin("shop2_auth", "123456");
        //删除前
        String result = new String(Objects.requireNonNull(this.gatewayClient.get().uri(DEPARTURL +"?page=1&pageSize=50" , 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == '17333' && @.user.id == '17352')].user.id").exists()
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        List<UserProxyRetVo> list = JacksonUtil.parseSubnodeToObjectList(result,"/data/list",UserProxyRetVo.class);
        Long id = null;
        for (UserProxyRetVo vo : list){
            if (vo.getProxyUser().getId().equals(17333L) && vo.getUser().getId().equals(17352L)){
                id = vo.getId();
                break;
            }
        }
        assertNotNull(id);

        String token1 = this.adminLogin("proxy_user2", "123456");
        //删除前有权限
        this.gatewayClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        String token3 = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(DEPARTIDURL, 2, id)
                .header("authorization", token3)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
                
        //删除后无权限
        this.gatewayClient.get().uri(DEPARTURL, 2)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

    /**
     * 2
     * 删除自己的用户代理关系
     * 在createProxies4之后运行
     */
    @Test
    @Order(6)
    public void delSelfProxies6() throws Exception {

        String token = this.adminLogin("shop1_auth", "123456");
        //删除前
        String result = new String(Objects.requireNonNull(this.gatewayClient.get().uri(SELFPROXYURL)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.proxyUser.id == 17330)].user.id").isEqualTo(17351)
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        List<UserProxyRetVo> list = JacksonUtil.parseSubnodeToObjectList(result,"/data/list",UserProxyRetVo.class);
        Long id = null;
        for (UserProxyRetVo vo : list){
            if (vo.getProxyUser().getId().equals(17330L) && vo.getUser().getId().equals(17351L)){
                id = vo.getId();
                break;
            }
        }
        assertNotNull(id);

        String token1 = this.adminLogin("proxy_user1", "123456");
        //删除前有权限
        this.gatewayClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());


        this.gatewayClient.delete().uri(SELFIDURL, id)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //删除后无权限
        this.gatewayClient.get().uri(DEPARTURL, 1)
                .header("authorization", token1)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());

    }

}
