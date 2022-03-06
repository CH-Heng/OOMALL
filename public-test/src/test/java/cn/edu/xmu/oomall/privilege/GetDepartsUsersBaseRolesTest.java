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
public class GetDepartsUsersBaseRolesTest extends BaseTestOomall {

    private static final String TESTURL ="/privilege/departs/{did}/users/{id}/baseroles";

    /**
     * 29 管理员查看用户拥有的权限
     * 查找用户 Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserPrivs1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL+"?page=1&pageSize=4",0,1).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4);
    }

    /**
     * 30 同部门管理员查看用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserPrivs2() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(TESTURL+"?page=1&pageSize=4",1,46).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4);
    }

    /**
     * 31 不同部门管理员获得用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserPrivs3() throws Exception {
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(TESTURL,2,47).header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 不登录
     * @throws Exception
     */
    @Test
    public void findUserPrivs4() throws Exception {
        this.gatewayClient.get().uri(TESTURL,2,47)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_NEED_LOGIN.getCode());
    }

}
