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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = PublicTestApp.class)
public class LogoutTest extends BaseTestOomall {

    private static final String TESTURL ="/privilege/logout";

    private static final String NEWURL ="/privilege/departs/{did}/users/new";


    /**
     * @author Song Runhan
     * @date Created in 2020/11/4/ 16:00
     */
    @Test
    public void logout1() throws  Exception{
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;

        String token = this.adminLogin("13088admin","123456");

        //region 用户正常登出
        this.gatewayClient.get().uri(TESTURL)
                .header("authorization",token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient
                .get()
                .uri(NEWURL+"?userName=departuser2", 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_USER_FORBIDDEN.getCode());
    }


    @Test
    public void logout2() throws  Exception{
        String token = "this is test";

        this.gatewayClient.get().uri(TESTURL)
                .header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.AUTH_INVALID_JWT.getCode());
    }
}
