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
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderPayment1Test extends BaseTestOomall {
    private static final int RETRYTIME = 20;


    private static final String ORDER = "/order/orders";
    private static final String ID = "/order/orders/{id}";
    private static final String CANCAL = "/order/orders/{id}/cancel";
    private static final String CONFIRM = "/order/orders/{id}/confirm";
    private static final String SHOPORDER = "/order/shops/{shopId}/orders";
    private static final String SHOPORDERID = "/order/shops/{shopId}/orders/{id}";
    private static final String DELIVER = "/order/shops/{shopId}/orders/{id}/deliver";
    private static final String ORDERPAYMENT = "/order/orders/{id}/payment";
    private static final String REFUND = "/order/orders/{id}/refund";

    private static final String PAY = "/payment/payments";

    private static final String SELF = "/customer/self";

    private static String PAYFORMAT = "{\"patternId\": %d, \"documentId\": \"%s\", \"documentType\": %d, \"amount\": %d, \"beginTime\": \"%s\",  \"endTime\": \"%s\"}";

    private static String orderRet1 = null;
    private static Integer orderId1 = null;

    /**
     * 先看看自己多少订单，其实啥也没有
     * 1001用户
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getOrder1() throws Exception {
        String token = this.customerLogin("685258", "123456");
        this.mallClient.get().uri(ORDER)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(0);
    }

    /**
     * 先看看自己多少点，错误计算在其他模块
     * 2261
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void CustomerSelf1() throws Exception {
        String token = this.customerLogin("685258", "123456");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1000)
                .jsonPath("$.data.userName").isEqualTo("685258")
                .jsonPath("$.data.name").isEqualTo("李存伟")
                .jsonPath("$.data.mobile").isEqualTo("13959207496")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(2261);
    }

    /**
     * 下商铺1订单，
     * 无活动，无优惠，无不同运费模板 小儿科
     * 1000用户
     * 翔安区
     * 100积点支付
     * String json ="{\"orderItems\": [\n" +
     * "    {\"productId\": 4226, \"onsaleId\": 2667,  \"quantity\": 10,   \"couponActId\": 0, \"couponId\": 0}" +
     * "  ], \"consignee\": \"string\",\"regionId\": 0, \"address\": \"string\", \"mobile\": \"string\", \"message\": \"string\", \"advancesaleId\": 0, \"grouponId\": 0, \"point\": 0}";
     *
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postOrder() throws Exception {
        String token = this.customerLogin("685258", "123456");
        String json = "{\"orderItems\": [\n" +
                "{\"productId\": 4226, \"onsaleId\": 2677,  \"quantity\": 2}," +
                "{\"productId\": 4264, \"onsaleId\": 2715,  \"quantity\": 1}" +
                "  ], \"consignee\": \"李存伟\",\"regionId\": 1604, \"address\": \"翔安南路1号\", \"mobile\": \"13959207496\", \"point\": 100}";
        String ret = new String(Objects.requireNonNull(this.mallClient.post().uri(ORDER)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(1)
                //7041*2+20207*1-100(point) + 1000(expressFee)
                .jsonPath("$.data[0].amount").isEqualTo(35189)
                .returnResult().getResponseBody()), "UTF-8");

        orderRet1 = JacksonUtil.parseSubnodeToString(ret, "/data");
        /*   freight model 1 1604 翔安区 - 151 厦门市
                              4226  4264
                    weight    30    118
                    quantity  2      1
                    total      60+118=178
                    ExpressFee   1000
        */
    }

    /**
     * 立刻支付宝支付
     * 看谁手快
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void payOrder1() throws Exception {
        assertNotNull(orderRet1);
        String orderSn = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/orderSn", String.class);
        Integer amount = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/amount", Integer.class);
        String beginTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/beginTime", String.class);
        String endTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/endTime", String.class);
        assertEquals(35189, amount);

        String token = this.customerLogin("685258", "123456");

        String json = String.format(PAYFORMAT, 0, orderSn, 0, amount, beginTime, endTime);
        this.mallClient.post().uri(PAY)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }

    /**
     * 再看看自己多少点，错误计算在订单模块
     * 2261 -100
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void CustomerSelf2() throws Exception {
        String token = this.customerLogin("685258", "123456");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1000)
                .jsonPath("$.data.userName").isEqualTo("685258")
                .jsonPath("$.data.name").isEqualTo("李存伟")
                .jsonPath("$.data.mobile").isEqualTo("13959207496")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(2161);
    }

    /**
     * 再看看自己订单，该有了吧
     * 如果没有就使劲看，四次看不到，算你错
     * 1000用户
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getOrder2() throws Exception {
        String token = this.customerLogin("685258", "123456");
        Integer orderNum = 0;
        int time = 0;
        List<String> ret = null;
        while (0 == orderNum && time < RETRYTIME) {
            String res = new String(Objects.requireNonNull(this.mallClient.get().uri(ORDER)
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF-8");
            ret = JacksonUtil.parseSubnodeToStringList(res, "/data/list");
            orderNum = ret.size();
            if (orderNum == 0){
                Thread.sleep(5000);
            }
            time++;
        }
        assertEquals(1, orderNum);
        orderId1 = JacksonUtil.parseInteger(ret.get(0), "id");
        assertEquals(1000, JacksonUtil.parseInteger(ret.get(0), "customerId"));
    }

    /**
     * 看看订单支付
     * 如果没有支付没成功，就使劲支付，到成功为止
     * 2261
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getPayment() throws Exception {
        assertNotNull(orderId1);
        assertNotNull(orderRet1);
        String sn = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/orderSn", String.class);
        Integer amount = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/amount", Integer.class);
        String beginTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/beginTime", String.class);
        String endTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/endTime", String.class);
        assertEquals(35189, amount);

        String token = this.customerLogin("685258", "123456");
        int state = 5;
        int time = 0;

        //支付失败试四次，四次都失败运气太差了，算你输
        while (5 == state && time < RETRYTIME) {
            String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(ORDERPAYMENT, orderId1)
                    .header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/json;charset=UTF-8")
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");

            List<String> payments = JacksonUtil.parseSubnodeToStringList(ret, "/data");
            state = 5;
            for (String payment: payments){
                state = JacksonUtil.parseSubnodeToObject(payment, "/state", Integer.class);
                if (state == 1){
                    //寻找是否有成功的支付
                    break;
                }
            }

            if (5 == state) {
                //支付失败再支付一次
                String json = String.format(PAYFORMAT, 0, sn, 0, amount, beginTime, endTime);
                this.mallClient.post().uri(PAY)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(json)
                        .exchange()
                        .expectHeader()
                        .contentType("application/json;charset=UTF-8")
                        .expectStatus().isCreated()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
            }else if(0 == state){
                //休眠1s
                Thread.sleep(5000);
                state = 5;
            }
            time++;
        }
    }
    /**
     * 再看看自己订单，
     * 见证奇迹的时刻
     * 1000用户
     *
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getOrder3() throws Exception {
        assertNotNull( orderId1);
        String token = this.customerLogin("685258", "123456");

        //等待订单支付成功
        Integer state = 101;
        int times = 0;

        while (101 == state && times < RETRYTIME) {
            String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(ID, orderId1)
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");

            state = JacksonUtil.parseSubnodeToObject(ret, "/data/state", Integer.class);
            if (101 == state){
                Thread.sleep(5000);
            }
            times++;
        }

        this.mallClient.get().uri(ID, orderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.pid").isEqualTo(0)
                .jsonPath("$.data.originPrice").isEqualTo(34289)
                .jsonPath("$.data.discountPrice").isEqualTo(0)
                .jsonPath("$.data.expressFee").isEqualTo(1000)
                .jsonPath("$.data.point").isEqualTo(100)
                .jsonPath("$.data.regionId").isEqualTo(1604)
                .jsonPath("$.data.consignee").isEqualTo("李存伟")
                .jsonPath("$.data.mobile").isEqualTo("13959207496")
                .jsonPath("$.data.address").isEqualTo("翔安南路1号")
                .jsonPath("$.data.grouponId").isEqualTo(0)
                .jsonPath("$.data.advancesaleId").isEqualTo(0)
                .jsonPath("$.data.state").isEqualTo(201)
                .jsonPath("$.data.orderItem.length()").isEqualTo(2)
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].name").isEqualTo("振能盆")
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].quantity").isEqualTo(2)
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].price").isEqualTo(7041)
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].discountPrice").isEqualTo(0)
                // 算给邮费了
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].point").isEqualTo(0)
                .jsonPath("$.data.orderItem[?(@.productId == '4264')].name").isEqualTo("肤歌幽兰除菌皂")
                .jsonPath("$.data.orderItem[?(@.productId == '4264')].quantity").isEqualTo(1)
                .jsonPath("$.data.orderItem[?(@.productId == '4264')].price").isEqualTo(20207)
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].discountPrice").isEqualTo(0)
                // 算给邮费了
                .jsonPath("$.data.orderItem[?(@.productId == '4226')].point").isEqualTo(0);
    }

    /**
     * 店家发货
     * @throws Exception
     */
    @Test
    @Order(8)
    public void deliverOrder() throws Exception {
        assertNotNull(orderId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"shipmentSn\": \"JD123456\" }";
        this.gatewayClient.put().uri(DELIVER,1,orderId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 发货后的订单
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getOrder4() throws Exception {
        assertNotNull( orderId1);
        String token = this.customerLogin("685258", "123456");
        this.mallClient.get().uri(ID,orderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(300)
                .jsonPath("$.data.shipmentSn").isEqualTo("JD123456");
    }
    /**
     * 用户签收
     * @throws Exception
     */
    @Test
    @Order(10)
    public void confirmOrder() throws Exception {
        assertNotNull( orderId1);
        String token = this.customerLogin("685258", "123456");
        this.mallClient.put().uri(CONFIRM,orderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }
    /**
     * 签收后的订单
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getShopOrder4() throws Exception {
        assertNotNull( orderId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(SHOPORDERID,1, orderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(400);
    }
}
