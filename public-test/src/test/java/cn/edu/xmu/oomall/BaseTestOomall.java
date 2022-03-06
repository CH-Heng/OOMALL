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

package cn.edu.xmu.oomall;

import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public abstract class BaseTestOomall implements InitializingBean {

    @Value("${public-test.gateway-gate}")
    protected String gateway;

    @Value("${public-test.mall-gate}")
    protected String mall;

    protected WebTestClient gatewayClient;

    protected WebTestClient mallClient;

    public static final String ADMINTEMP = "{\"name\":\"%s\",\"password\":\"%s\"}";
    public static final String CUSTOMERTEMP = "{\"userName\":\"%s\",\"password\":\"%s\"}";
    private static final String CUSTOMERLOGIN ="/customer/login";
    private static final String ADMINLOGIN ="/privilege/login";


    @Override
    public void afterPropertiesSet() throws Exception {
        this.gatewayClient = WebTestClient.bindToServer()
                .responseTimeout(Duration.ofSeconds(10))
                .baseUrl("http://"+gateway)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .responseTimeout(Duration.ofSeconds(10))
                .baseUrl("http://"+mall)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    protected String adminLogin(String userName, String password){
        String requireJson = String.format(ADMINTEMP,userName,password);
        byte[] ret = gatewayClient.post().uri(ADMINLOGIN)
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret), "data");
        //endregion
    }

    protected String customerLogin(String userName, String password){
        String requireJson  = String.format(CUSTOMERTEMP, userName, password);
        byte[] ret = this.mallClient.post().uri(CUSTOMERLOGIN)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.oomall.core.util.ReturnNo.OK.getCode())
                .returnResult().getResponseBodyContent();

        return JacksonUtil.parseString(new String(ret), "data");
    }

}
