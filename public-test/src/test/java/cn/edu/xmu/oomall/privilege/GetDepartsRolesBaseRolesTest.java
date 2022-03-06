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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetDepartsRolesBaseRolesTest extends BaseTestOomall {

    private static final String TESTURL ="/privilege/departs/{did}/roles/{id}/baseroles";

    /**
     * 32 管理员查看角色拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void getRoleBaseroles1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL, 1, 104 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '92')]").exists()
                .jsonPath("$.data.list[?(@.id == '93')]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }

    /**
     * 33 同部门管理员查看角色拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void getRoleBaseroles2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(TESTURL, 1, 104 ).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '92')]").exists()
                .jsonPath("$.data.list[?(@.id == '93')]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(2);
    }

    /**
     * 34 不同部门管理员获得用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void getRoleBaseroles3() throws Exception {
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(TESTURL, 1, 104 ).header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 35 管理员查看角色拥有的权限
     *  Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserRolePrivs1() throws Exception {
        this.gatewayClient.get().uri(TESTURL, 1, 104 )
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
}
