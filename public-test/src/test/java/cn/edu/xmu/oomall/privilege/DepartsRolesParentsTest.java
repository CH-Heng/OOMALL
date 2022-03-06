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
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsRolesParentsTest extends BaseTestOomall {

    private static final String PARENTURL = "/privilege/departs/{did}/roles/{id}/parents";

    private static final String ROLEINHURL = "/privilege/departs/{did}/roles/{pid}/childroles/{cid}";

    private static final String BASEROLEURL ="/privilege/departs/{did}/roles/{id}/baseroles";


    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void getRoleParentsTest1() throws Exception {
        this.gatewayClient.get().uri(PARENTURL,1, 2)
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
    public void getRoleParentsTest2() throws Exception {
        this.gatewayClient.get().uri(PARENTURL,1, 2)
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
    public void getRoleParentsTest3() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 2)
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
    public void getRoleParentsTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 2)
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
    public void getRoleParentsTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '88') ]").exists()
                .jsonPath("$.data.list[?(@.id == '89') ]").exists()
                .jsonPath("$.data.list[?(@.id == '90') ]").exists()
                .jsonPath("$.data.list[?(@.id == '91') ]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getRoleParents6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(PARENTURL,2, 105)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '92') ]").exists()
                .jsonPath("$.data.list[?(@.id == '93') ]").exists();
    }

    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void postRoleInheritedTest1() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.post().uri(ROLEINHURL,1, 111,119)
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
    public void postRoleInheritedTest2() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.post().uri(ROLEINHURL,1, 111,119)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }


    /**
     * 33 同部门管理员查看角色拥有的权限
     * @throws Exception
     */
    @Test
    @Order(2)
    public void findRolePrivs1() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(BASEROLEURL, 1, 119 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

    /**
     * 部门管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postRoleInheritedTest3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(PARENTURL,1, 119)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '111') ]").doesNotExist();

        this.gatewayClient.post().uri(ROLEINHURL,1, 111,119)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PARENTURL,1, 119)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '111') ]").exists();
    }

    /**
     * 33 同部门管理员查看角色拥有的权限
     * @throws Exception
     */
    @Test
    @Order(4)
    public void findRolePrivs2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(BASEROLEURL, 1, 119 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '97')]").exists()
                .jsonPath("$.data.list[?(@.id == '99')]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }

    /**
     * 33 同部门管理员查看角色拥有的权限
     * @throws Exception
     */
    @Test
    @Order(5)
    public void findRolePrivs3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(BASEROLEURL, 2, 120 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }


    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(6)
    public void postRoleInheritedTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(PARENTURL,2, 120)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '112') ]").doesNotExist();

        this.gatewayClient.post().uri(ROLEINHURL,2, 112,120)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PARENTURL,2, 120)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '112') ]").exists();
    }

    /**
     * 33 同部门管理员查看角色拥有的权限
     * @throws Exception
     */
    @Test
    @Order(7)
    public void findRolePrivs4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(BASEROLEURL, 2, 120 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '97')]").exists()
                .jsonPath("$.data.list[?(@.id == '99')]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }

}
