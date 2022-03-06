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
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
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
public class CustomerTest extends BaseTestOomall {
    private static final String STATES = "/customer/customers/states";
    private static final String LOGIN ="/customer/login";
    private static final String LOGOUT ="/customer/logout";
    private static final String SELF ="/customer/self";
    private static final String POST ="/customer/customers";
    private static final String PASSWD ="/customer/password";
    private static final String RESET ="/customer/password/reset";
    private static final String ALL ="/customer/shops/{id}/customers";
    private static final String ID ="/customer/shops/{shopId}/customers/{id}";
    private static final String BAN ="/customer/shops/{shopId}/customers/{id}/ban";
    private static final String RELEASE ="/customer/shops/{shopId}/customers/{id}/release";

    private static Integer customerId1 = null;
    /**
     * 获得的所有状态
     * @throws Exception
     */
    @Test
    public void getState() throws Exception {
        this.mallClient.get().uri(STATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(2);
    }
    /**
     * @author Song Runhan
     *         //region 密码错误的用户登录
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login2() throws Exception {
        String requireJson = "{\"userName\":\"customer1\",\"password\":\"000000\"}";
        this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_INVALID_ACCOUNT.getCode());
    }
    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login3() throws Exception {
        String requireJson = "{\"userName\":\"NotExist\",\"password\":\"123456\"}";
        this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_INVALID_ACCOUNT.getCode());
    }
    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login4() throws Exception {
        String requireJson = "{\"password\":\"123456\"}";
        //region 没有输入用户名的用户登录
        this.mallClient.post().uri(LOGIN)
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
        String requireJson = "{\"userName\":\"537300010\",\"password\":\"\"}";
        this.mallClient.post().uri(LOGIN)
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
        String requireJson  = "{\"userName\":\"customer1\",\"password\":\"123456\"}";
        byte[] response = this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent();

        byte[] response1 = this.mallClient.post().uri(LOGIN)
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
        String requireJson  = "{\"userName\":\"575332\",\"password\":\"123456\"}";
        this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_FORBIDDEN.getCode());
    }

    @Test
    public void logout2() throws  Exception{
        String token = "this is test";

        this.mallClient.get().uri(LOGOUT)
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 电话号码重复
     * @throws Exception
     */
    @Test
    public void postCustomer1() throws Exception {
        String requireJson="{\"userName\": \"mybabyw2\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13959235540\", \"email\": \"t223e21st2jcs@test.com\"}";
        this.mallClient.post().uri(POST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_MOBILEEXIST.getCode());
    }


    /**
     * 与新用户表用户名重复
     * @throws Exception
     */
    @Test
    public void postCustomer2() throws Exception {
        String requireJson="{\"userName\": \"105048\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13159235540\", \"email\": \"t223e21st2jcs@test.com\"}";
        this.mallClient.post().uri(POST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_NAMEEXIST.getCode());
    }
    /**
     * 与新用户表EMail重复
     * @throws Exception
     */
    @Test
    public void postCustomer3() throws Exception {
        String requireJson="{\"userName\": \"a105048\",\"password\": \"AaBD11231!!\", \"name\": \"LiangJi1\",   \"mobile\": \"13159235540\", \"email\": \"test@email.com\"}";
        this.mallClient.post().uri(POST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_EMAILEXIST.getCode());
    }
    /**
     *空用户名
     * @throws Exception
     */
    @Test
    public void postCustomer4() throws Exception {
        String requireJson="{\n    \"userName\": null,\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",    \"mobile\": \"6411686886\",  \"email\": \"test@test.com\"}";
        this.mallClient.post().uri(POST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }
    /**
     * 正常注册
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postCustomer5() throws Exception {
        String requireJson="{ \"userName\": \"customer3\", \"password\": \"Aa123456!@#\",  \"name\": \"测试人\",    \"mobile\": \"13822888388\",  \"email\": \"test@test1.com\"}";
        String ret = new String(Objects.requireNonNull(this.mallClient.post().uri(POST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent()),"UTF-8");

        this.customerId1 = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Integer.class);

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(ID,0, this.customerId1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试人");
    }

    /**
     * 未登录测试
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getSelf1() throws Exception {
        this.mallClient.get().uri(SELF)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 错误的token
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getSelf2() throws Exception {
        String token = "hello";
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
    /**
     * 正常
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getSelf3() throws Exception {
        String token = this.customerLogin("customer3","Aa123456!@#");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.customerId1)
                .jsonPath("$.data.userName").isEqualTo("customer3")
                .jsonPath("$.data.name").isEqualTo("测试人")
                .jsonPath("$.data.mobile").isEqualTo("13888888388")
                .jsonPath("$.data.email").isEqualTo("test@test1.com")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(0);
    }
    /**
     * 修改，不能改其他
     * @throws Exception
     */
    @Test
    @Order(4)
    public void putSelf1() throws Exception {
        String token = this.customerLogin("customer3","Aa123456!@#");
        String json="{ \"userName\": \"customer31\", \"password\": \"1234561\",  \"name\": \"测试人1\",    \"mobile\": \"138888883881\",  \"email\": \"test@test2.com\"}";
        this.mallClient.put().uri(SELF)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        this.mallClient.get().uri(LOGOUT)
                .header("authorization",token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        token = this.customerLogin("customer3","Aa123456!@#");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.customerId1)
                .jsonPath("$.data.userName").isEqualTo("customer3")
                .jsonPath("$.data.name").isEqualTo("测试人1")
                .jsonPath("$.data.mobile").isEqualTo("13888888388")
                .jsonPath("$.data.email").isEqualTo("test@test1.com")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(0);
    }
    /**
     * 重置密码-与原密码相同
     * @throws Exception
     */
    @Test
    @Order(4)
    public void resetPassTest1() throws Exception {
        String json = "{\"name\": \"404935\"}";
        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESET)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseSubnodeToObject(ret, "/data/captcha", String.class);
        json = String.format("{\"userName\": \"404935\", \"captcha\": \"%s\", \"newPassword\": \"%s\"}", captcha, "Aa123456!@#");
        this.mallClient.put().uri(PASSWD)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_PASSWORDSAME.getCode());
    }
    /**
     * 重置密码-用户名不存在
     * @throws Exception
     */
    @Test
    public void resetPassTest2() throws Exception {
        String json = "{\"name\": \"nononocustomer3\"}";
        this.mallClient.put().uri(RESET)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMERID_NOTEXIST.getCode());
    }
    /**
     * 重置密码-验证码不对
     * @throws Exception
     */
    @Test
    public void resetPassTest3() throws Exception {
        String json = "{\"name\": \"808893\"}";
        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESET)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseSubnodeToObject(ret, "/data/captcha", String.class);
        json = String.format("{\"userName\": \"808893\", \"captcha\": \"%s\", \"newPassword\": \"%s\"}", "captcha", "Aa123456!@#");
        this.mallClient.put().uri(PASSWD)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMERID_NOTEXIST.getCode());
    }
    /**
     * 重置密码 - 超时
     * @throws Exception
     */
    @Test
    public void resetPassTest4() throws Exception {
        String json = "{\"name\": \"493381\"}";
        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESET)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseSubnodeToObject(ret, "/data/captcha", String.class);
        json = String.format("{\"userName\": \"493381\", \"captcha\": \"%s\", \"newPassword\": \"%s\"}", captcha, "Aa123456!@#");
        Thread.sleep(31000);
        this.mallClient.put().uri(PASSWD)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMERID_NOTEXIST.getCode());
    }
    /**
     * 重置密码
     * @throws Exception
     */
    @Test
    public void resetPassTest5() throws Exception {
        String json = "{\"name\": \"537216\"}";
        String ret = new String(Objects.requireNonNull(this.mallClient.put().uri(RESET)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBody()), "UTF-8");

        String captcha = JacksonUtil.parseSubnodeToObject(ret, "/data/captcha", String.class);
        json = String.format("{\"userName\": \"537216\", \"captcha\": \"%s\", \"newPassword\": \"%s\"}", captcha, "Aa123456!@#");
        this.mallClient.put().uri(PASSWD)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        this.customerLogin("537216","Aa123456!@#");
    }
    /**
     * 获得用户
     * @throws Exception
     */
    @Test
    public void getCustomer1() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.get().uri(ALL+"?userName=699275", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '赵永波')]").exists();
    }
    /**
     * 获得用户
     * @throws Exception
     */
    @Test
    public void getCustomer2() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.get().uri(ALL+"?mobile=13959298012", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '赵俊峻')]").exists();
    }
    /**
     * 获得用户3
     * @throws Exception
     */
    @Test
    public void getCustomer3() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.get().uri(ALL+"?mobile=139592980122", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }
    /**
     * 获得用户
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getCustomer4() throws Exception {
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.get().uri(ALL+"?email=test@test1.com", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试人1')]").exists();
    }
    /**
     * 禁止用户
     * @throws Exception
     */
    @Test
    @Order(6)
    public void banCustomer1() throws Exception {
//        assertNotNull(this.customerId1);
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.put().uri(BAN, 0, this.customerId1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String requireJson  = "{\"userName\":\"customer3\",\"password\":\"Aa123456!@#\"}";
        this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.CUSTOMER_FORBIDDEN.getCode());
    }
    /**
     * 解禁用户
     * @throws Exception
     */
    @Test
    @Order(7)
    public void releaseCustomer1() throws Exception {
        assertNotNull(this.customerId1);
        String token = this.adminLogin("13088admin","123456");
        this.gatewayClient.put().uri(RELEASE, 0, this.customerId1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String requireJson  = "{\"userName\":\"customer3\",\"password\":\"Aa123456!@#\"}";
        this.mallClient.post().uri(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
}
