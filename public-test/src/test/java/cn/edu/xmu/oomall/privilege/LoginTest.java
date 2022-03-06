package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = PublicTestApp.class)
public class LoginTest extends BaseTestOomall {

    private static final String TESTURL ="/privilege/login";

    /**
     * 签名错误的用户
     * @throws Exception
     */
    @Test
    public void login1() throws Exception {
        String requireJson = "{\"name\":\"wrong_sign\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_FALSIFY.getCode());
    }


    /**
     * @author Song Runhan
     *         //region 密码错误的用户登录
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login2() throws Exception {
        String requireJson = "{\"name\":\"13088admin\",\"password\":\"000000\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login3() throws Exception {
        String requireJson = "{\"name\":\"NotExist\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login4() throws Exception {
        String requireJson = null;
        WebTestClient.RequestHeadersSpec res = null;

        //region 没有输入用户名的用户登录
        requireJson = "{\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * @author Song Runhan
     * //region 没有输入密码（密码空）的用户登录
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login5() throws Exception {
        String requireJson = "{\"name\":\"537300010\",\"password\":\"\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login6() throws Exception {
        String requireJson  = "{\"name\":\"13088admin\",\"password\":\"123456\"}";
        byte[] response = this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent();

        byte[] response1 = this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent();

        String jwt = JacksonUtil.parseString(new String(response,"UTF-8"), "data");
        String jwt1 = JacksonUtil.parseString(new String(response1,"UTF-8"), "data");
        assertNotEquals(jwt, jwt1);
    }

    /**
     * 被封禁的用户
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login7() throws Exception {
        String requireJson  = "{\"name\":\"5264500009\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
    }

    /**
     * 用邮箱登录
     */
    @Test
    public void login8() throws Exception {

        String requireJson =  "{\"name\":\"asdewetsa@tttt\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
}

    /**
     * 电话登录
     */
    @Test
    public void login9() throws Exception {

        String requireJson =  "{\"name\":\"223472349907788\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());
    }

    /**
     * 用邮箱登录
     */
    @Test
    public void login10() throws Exception {
        //delrole_user1
        String requireJson =  "{\"name\":\"3s@12dr\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 电话登录
     */
    @Test
    public void login11() throws Exception {
        //delrole_user2
        String requireJson =  "{\"name\":\"331434254\",\"password\":\"123456\"}";
        this.gatewayClient.post().uri(TESTURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

}
