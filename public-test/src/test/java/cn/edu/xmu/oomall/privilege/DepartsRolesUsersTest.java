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

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsRolesUsersTest extends BaseTestOomall {

    private static final String ROLEUSER = "/privilege/departs/{did}/roles/{id}/users";

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    public void selectRoleUsersTest1() throws Exception {
        this.gatewayClient.get().uri(ROLEUSER,1, 2)
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
    public void selectRoleUsersTest2() throws Exception {
        this.gatewayClient.get().uri(ROLEUSER,1, 2)
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
    public void selectRoleUsersTest3() throws Exception {
        String token = this.adminLogin("shop1_coupon", "123456");

        this.gatewayClient.get().uri(ROLEUSER,1, 2)
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
    public void selectRoleUsersTest4() throws Exception {
        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.get().uri(ROLEUSER,1, 2)
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
    public void selectRoleUsersTest5() throws Exception {
        String token = this.adminLogin("8131600001", "123456");

        this.gatewayClient.get().uri(ROLEUSER,1, 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '46')]").exists();
    }

    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void selectRoleUsersTest6() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get().uri(ROLEUSER,2, 3)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '47')]").exists();
    }

}
