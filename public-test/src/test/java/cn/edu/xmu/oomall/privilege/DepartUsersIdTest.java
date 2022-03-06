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
import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Objects;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartUsersIdTest extends BaseTestOomall {

    private static final  String TESTURL ="/privilege/departs/{did}/users/{id}";
    private static final String IDURL = "/privilege/departs/{did}/users/{id}";
    private static final String GETURL = "/privilege/users";
    private static final String NEWUSERURL = "/privilege/departs/{did}/newusers/{id}";
    private static final String APPROVEUSERURL = "/privilege/departs/{did}/newusers/{id}/approve";
    private static final String USERURL = "/privilege/departs/{did}/users";
    private static final String FORBIDURL = "/privilege/departs/{did}/users/{id}/forbid";
    private static final String RELEASEURL = "/privilege/departs/{did}/users/{id}/release";
    /***
     * 查找用户
     * @throws Exception
     */
    @Test
    @Order(1)
    public void findUserById1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL,1,46)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.userName").isEqualTo("8131600001")
                .jsonPath("$.data.sign").isEqualTo(0);

    }

    /***
     * 查找不存在的用户
     * @throws Exception
     */
    @Test
    public void findUserById2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL,0,23)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /***
     * 查找签名错误用户信息
     * @throws Exception
     */
    @Test
    public void findUserById3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL,1, 17341)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_FALSIFY.getCode());
    }

    /***
     * 查找其他店铺用户信息
     * @throws Exception
     */
    @Test
    public void findUserById4() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL,1,17342)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 14
     * 无权限更新用户
     * @throws Exception
     */
    @Test
    public void modifyUser1() throws Exception{
        String token =this.adminLogin("shop1_coupon", "123456");
        String regJson = "{\"name\": \"testU1\",\"email\": \"assde@1121123\", \"mobile\": \"88663431122\"}";
        this.gatewayClient.put().uri(TESTURL, 1, 17351)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 18
     * 更新用户数据，不存在此用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void modifyUser6() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        String regJson = "{\"name\": \"testU\",\"email\": \"test@test.cn\", \"mobile\": \"11111111111\"}";
        this.gatewayClient.put().uri(TESTURL, 1, 99999)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 14
     * 不同店铺管理员更新用户
     * @throws Exception
     */
    @Test
    public void modifyUser7() throws Exception{
        String token =this.adminLogin("2721900002", "123456");
        String regJson = "{\"name\": \"testU1\",\"idNumber\": \"3701091234343424\"}";
        this.gatewayClient.put().uri(TESTURL, 1, 17349)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 21
     * 平台管理员修改用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(2)
    public void modifyUser8() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        String regJson = "{\"name\": \"testU123\", \"idNumber\": \"3701091234343424\"}";
        this.gatewayClient.put().uri(TESTURL, 1,17349)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.put().uri(IDURL, 1,17349)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 21
     * 店铺管理员修改用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(2)
    public void modifyUser9() throws Exception{
        String token =this.adminLogin("2721900002", "123456");

        String regJson = "{\"name\": \"testU222\", \"idNumber\": \"37010912343434242\"}";
        this.gatewayClient.put().uri(TESTURL, 2,17350)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(IDURL, 2,17350)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(17350)
                .jsonPath("$.data.name").isEqualTo("testU222")
                .jsonPath("$.data.idNumber").isEqualTo("37010912343434242")
                .jsonPath("$.data.sign").isEqualTo(0);
    }

    /**
     * 与新用户表电话号码重复
     * @throws Exception
     */
    @Test
    public void register1() throws Exception {
        String requireJson="{\n    \"userName\": \"mybabyw2\",\n    \"password\": \"AaBD11231!!\",\n    \"name\": \"LiangJi1\",   \"mobile\": \"21433452556334\",\n    \"email\": \"t223e21st2jcs@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.MOBILE_REGISTERED.getCode());
    }


    /**
     * 与新用户表用户名重复
     * @throws Exception
     */
    @Test
    public void register2() throws Exception {
        String requireJson="{\n    \"userName\": \"mybaby\",\n    \"password\": \"AaBD11231!!\",\n    \"name\": \"LiangJi1\",   \"mobile\": \"62231241168683243243236\",\n    \"email\": \"t223est2jcs@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.USER_NAME_REGISTERED.getCode());
    }
    /**
     * 与新用户表EMail重复
     * @throws Exception
     */
    @Test
    public void register3() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"mobile\": \"262231241168683243243236\",  \"email\": \"mybaby@test1.com\",  \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.EMAIL_REGISTERED.getCode());
    }

    /**
     *空用户名
     * @throws Exception
     */
    @Test
    public void register4() throws Exception {
        String requireJson="{\n    \"userName\": null,\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",  \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());

    }

    /**
     * 用户名长度过短
     * @throws Exception
     */
    @Test
    public void register5() throws Exception {
        String requireJson="{\n    \"userName\": \"13087\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",   \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",  \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * 密码格式错误
     * @throws Exception
     */
    @Test
    public void register6() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123123\",\n    \"name\": \"LiangJi\",   \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * 名称为空
     * @throws Exception
     */
    @Test
    public void register7() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": null,   \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }

    /**
     * email为空
     * @throws Exception
     */
    @Test
    public void register9() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",  \"mobile\": \"6411686886\",\n    \"email\": null,  \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FIELD_NOTVALID.getCode());
    }


    /**
     * 与用户表用户名重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister4() throws Exception {
        String requireJson="{\n    \"userName\": \"13088admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",   \"mobile\": \"641168683243243236\",\n    \"email\": \"test2jcs@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.USER_NAME_REGISTERED.getCode());
    }
    /**
     * 与用户表电话重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister5() throws Exception {
        String requireJson="{\n    \"userName\": \"13089admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",    \"mobile\": \"12334555\",\n    \"email\": \"test0112@test.com\",  \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.MOBILE_REGISTERED.getCode());
    }
    /**
     * 与用户表email重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister6() throws Exception {
        String requireJson="{\n    \"userName\": \"13089admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",  \"mobile\": \"16978933874\",\n    \"email\": \"admin@112\", \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.EMAIL_REGISTERED.getCode());
    }

    /**
     * 重复的用户名
     * @throws Exception
     */
    @Test
    public void duplicateRegister1() throws Exception {
        String requireJson="{\n    \"userName\": \"130871234451admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",   \"mobile\": \"6411686836\",\n    \"email\": \"test2@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.USER_NAME_REGISTERED.getCode());
    }

    /**
     * 重复的电话
     * @throws Exception
     */
    @Test
    public void duplicateRegister2() throws Exception {
        String requireJson="{\n    \"userName\": \"duplicateTest2\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\", \"mobile\": \"642234811686886\",\n    \"email\": \"test3@test.com\",\n    \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.MOBILE_REGISTERED.getCode());
    }

    /**
     * 重复的email
     * @throws Exception
     *
     */
    @Test
    public void duplicateRegister3() throws Exception {
        String requireJson="{\n    \"userName\": \"duplicateTest7\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",  \"mobile\": \"6413683356846\",\n    \"email\": \"t112434est@test.com\",   \"departId\": 1\n}";
        this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.EMAIL_REGISTERED.getCode());
    }


    /**
     * 正常注册
     * @throws Exception
     */
    @Test
    @Order(3)
    public void register14() throws Exception {
        String requireJson="{ \"userName\": \"anormalusername3\", \"password\": \"1234aBa!\",  \"name\": \"LiangJi\",    \"mobile\": \"13888888388\",  \"email\": \"test@test.com\",  \"idNumber\": \"55983632754584\",    \"departId\": 1}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent()),"UTF-8");

        UserSimpleRetVo user = JacksonUtil.parseObject(ret, "data", UserSimpleRetVo.class);
        Long userId = user.getId();

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(NEWUSERURL,1,userId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("LiangJi");


        //不能登录
        requireJson = String.format(ADMINTEMP, "anormalusername3", "1234aBa!");
        gatewayClient.post().uri("/privilege/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());

        String json = "{\"approve\": true}";
        this.gatewayClient.put().uri(APPROVEUSERURL,1, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //可以登录
        gatewayClient.post().uri("/privilege/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 部门id为负数
     * @throws Exception
     */
    @Test
    @Order(3)
    public void register10() throws Exception {
        String requireJson="{\n    \"userName\": \"13087112admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"noshop\",  \"mobile\": \"64116222386886\",\n    \"email\": \"test@tes112344122t.com\",   \"departId\": -1}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent()),"UTF-8");

        UserSimpleRetVo user = JacksonUtil.parseObject(ret, "data", UserSimpleRetVo.class);
        Long userId = user.getId();

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(NEWUSERURL,-1, userId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("noshop");

        //不能登录
        requireJson = String.format(ADMINTEMP, "13087112admin", "AaBD1231!!");
        gatewayClient.post().uri("/privilege/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());

        String json = "{\"approve\": true}";
        this.gatewayClient.put().uri(APPROVEUSERURL,0, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //可以登录
        gatewayClient.post().uri("/privilege/login").
                bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 部门id为空， 应自动设定为-1
     * @throws Exception
     */
    @Test
    @Order(3)
    public void register11() throws Exception {
        String requireJson="{\n    \"userName\": \"130871234451admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"nullDepart\",  \"mobile\": \"642234811686886\",\n    \"email\": \"t112434est@test.com\",  \"departId\": null}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(GETURL)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode()).returnResult()
                .getResponseBodyContent()),"UTF-8");

        UserSimpleRetVo user = JacksonUtil.parseObject(ret, "data", UserSimpleRetVo.class);
        Long userId = user.getId();

        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(NEWUSERURL,-1, userId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("nullDepart");

        //不能登录
        requireJson = String.format(ADMINTEMP, "130871234451admin", "AaBD1231!!");
        gatewayClient.post().uri("/privilege/login").
                bodyValue(requireJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());

        String json = "{\"approve\": true}";
        this.gatewayClient.put().uri(APPROVEUSERURL,-1, userId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        //可以登录
        gatewayClient.post().uri("/privilege/login").
                bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

    }

    /**
     * 23
     * 测试封禁用户,非同店铺管理员
     * @throws Exception
     */
    @Test
    public void forbidUser1() throws Exception{
        String token =this.adminLogin("2721900002", "123456");

        this.gatewayClient.put().uri(FORBIDURL, 1,17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }



    /**
     * 23
     * 测试封禁用户,用户不存在（id不存在）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void forbidUser2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.put().uri(FORBIDURL, 1,999999)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }


    /**
     * 23
     * 测试封禁用户,店铺不存在这个商品
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void forbidUser3() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.put().uri(FORBIDURL, 2,17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 22
     * 测试封禁用户，平台管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(4)
    public void forbidUser5() throws Exception{
        //封禁前
        String requireJson = String.format(ADMINTEMP, "del_user1", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL,1,17357).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
    }

    /**
     * 22
     * 测试封禁已经封禁的用户，平台管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void forbidUser6() throws Exception{

        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(FORBIDURL,1,17357).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }


    /**
     * 22
     * 测试封禁用户，同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(6)
    public void forbidUser7() throws Exception{

        //封禁前
        String requireJson = String.format(ADMINTEMP, "del_user2", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        String token =this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(FORBIDURL,2,17358).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
    }

    /**
     * 25
     * 测试解禁用户，平台管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(7)
    public void releaseUser1() throws Exception{
        String requireJson = String.format(ADMINTEMP, "del_user1", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(RELEASEURL,1,17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //通过登录来验证是否成功
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 25
     * 测试解禁用户，店铺管理员
     * @throws Exception
     */
    @Test
    @Order(7)
    public void releaseUser2() throws Exception{
        String requireJson = String.format(ADMINTEMP, "del_user2", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
        String token =this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL,2,17358)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        //通过登录来验证是否成功
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 26
     * 测试解禁用户，用户不存在（id不存在）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void releaseUser3() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.put().uri(RELEASEURL,1,100909)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 26
     * 测试解禁用户，权限不够
     * @throws Exception
     */
    @Test
    public void releaseUser4() throws Exception{
        String token =this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.put().uri(RELEASEURL,1,17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 26
     * 测试解禁用户，非同店铺
     * @throws Exception
     */
    @Test
    public void releaseUser5() throws Exception{
        String token =this.adminLogin("shop2_adv", "123456");

        this.gatewayClient.put().uri(RELEASEURL,2,17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 25
     * 测试解禁用户，正常用户
     * @throws Exception
     */
    @Test
    @Order(8)
    public void releaseUser6() throws Exception{


        String token =this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(RELEASEURL,2,17358)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }

    /**
     * 20
     * 逻辑删除用户，用户不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void delUser1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.delete().uri(IDURL,1,10987)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 20
     * 逻辑删除用户，未登录
     * @throws Exception
     */
    @Test
    public void delUser2() throws Exception{

        this.gatewayClient.delete().uri(IDURL,1,10987)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

    /**
     * 20
     * 逻辑删除用户，不同部门
     * @throws Exception
     */
    @Test
    public void delUser3() throws Exception{
        String token =this.adminLogin("8131600001", "123456");

        this.gatewayClient.delete().uri(IDURL,2,47)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 20
     * 逻辑删除用户，无权限
     * @throws Exception
     */
    @Test
    public void delUser4() throws Exception{
        String token =this.adminLogin("shop2_adv", "123456");

        this.gatewayClient.delete().uri(IDURL,2,47)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NO_RIGHT.getCode());
    }

    /**
     * 20
     * 逻辑删除用户，token不对
     * @throws Exception
     */
    @Test
    public void delUser5() throws Exception{
        this.gatewayClient.delete().uri(IDURL,2,47)
                .header("authorization","hello")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }

    /**
     * 19
     * 逻辑删除用户, 平台管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(9)
    public void delUser6() throws Exception{
        //通过登录来验证是否成功
        String requireJson = String.format(ADMINTEMP, "del_user1", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL, 1,17357).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());
    }

    /**
     * 19
     * 逻辑删除用户, 同店铺管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(10)
    public void delUser7() throws Exception{
        //通过登录来验证是否成功
        String requireJson = String.format(ADMINTEMP, "del_user2", "123456");
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        String token =this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(IDURL, 2,17358).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        gatewayClient.post().uri("/privilege/login").bodyValue(requireJson).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_ACCOUNT.getCode());
    }

    /**
     * 24
     * 测试封禁用户,用户不存在（用户已被逻辑删除）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(11)
    public void forbidUser11() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.put().uri(FORBIDURL,1, 17357)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }


    /**
     * 27
     * 测试解禁用户，用户不存在（用户已被逻辑删除）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(12)
    public void releaseUser11() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.put().uri(RELEASEURL,2,17358)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 21
     * 平台管理员修改已删除用户
     * @throws Exception
     */
    @Test
    @Order(13)
    public void modifyUser11() throws Exception{
        String token =this.adminLogin("13088admin", "123456");

        String regJson = "{\"name\": \"testU123\", \"idNumber\": \"3701091234343424\"}";
        this.gatewayClient.put().uri(TESTURL, 1,17357)
                .header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 19
     * 逻辑删除已删除用户, 平台管理员
     * @throws Exception
     */
    @Test
    @Order(14)
    public void delUser11() throws Exception{


        String token =this.adminLogin("13088admin", "123456");

        this.gatewayClient.delete().uri(IDURL, 1,17357).header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
}
