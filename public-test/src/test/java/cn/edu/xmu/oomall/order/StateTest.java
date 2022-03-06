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

package cn.edu.xmu.oomall.order;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StateTest extends BaseTestOomall {
    private static final String PATTERNSTATES = "/payment/paypatterns/states";
    private static final String ORDERSTATES = "/order/orders/states";
    private static final String PAYMENTSTATES = "/payment/payment/states";
    private static final String REFUNDSTATES = "/payment/refund/states";
    /**
     * 获得的所有状态
     *
     * @throws Exception
     */
    @Test
    public void getPaymentState() throws Exception {
        this.mallClient.get().uri(PAYMENTSTATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(6);
    }

    /**
     * 获得的所有状态
     *
     * @throws Exception
     */
    @Test
    public void getRefundState() throws Exception {
        this.mallClient.get().uri(REFUNDSTATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(6);
    }

    /**
     * 获得的所有状态
     * 0 有效 1 无效
     * @throws Exception
     */
    @Test
    public void getPatternState() throws Exception {
        this.mallClient.get().uri(PATTERNSTATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    /**
     * 获得的所有状态
     *
     * @throws Exception
     */
    @Test
    public void getOrderState() throws Exception {
        this.mallClient.get().uri(ORDERSTATES)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(11);
    }
}
