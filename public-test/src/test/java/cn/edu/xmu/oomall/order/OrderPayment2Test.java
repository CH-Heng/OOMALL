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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderPayment2Test extends BaseTestOomall {
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
    private static final String COUPON = "/customer/coupons";

    private static String PAYFORMAT = "{\"patternId\": %d, \"documentId\": \"%s\", \"documentType\": %d, \"amount\": %d, \"beginTime\": \"%s\",  \"endTime\": \"%s\"}";

    //已有订单id
    private static final Set<Integer> orderIds = new HashSet<>() {{
        add(26376);
        add(26535);
        add(27118);
        add(30757);
    }};

    private static String orderRet1 = null;
    private static Integer orderId1 = null;
    private static Integer subOrderId1 = null;
    private static Integer subOrderId2 = null;
    private static Integer paymentId = null;

    /**
     * 先看看自己多少订单，4个已完成订单
     * 1001用户
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getOrder1() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(ORDER)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(4);
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
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1001)
                .jsonPath("$.data.userName").isEqualTo("696371")
                .jsonPath("$.data.name").isEqualTo("李存维")
                .jsonPath("$.data.mobile").isEqualTo("13959219573")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(2570);
    }

    /**
     * 再看看自己优惠卷，错误计算在其他模块
     * 两张，一张有效，一张失效
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void CustomerCoupon1() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(COUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '2')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(3);
    }

    /**
     * 下商铺1订单和商铺2的订单，
     * 分单，无活动，有优惠，无不同运费模板 难一点点
     * 1000用户
     * 泉州南安
     * 100积点支付
     * String json ="{\"orderItems\": [\n" +
     * "    {\"productId\": 4226, \"onsaleId\": 2667,  \"quantity\": 10,   \"couponActId\": 0, \"couponId\": 0}" +
     * "  ], \"consignee\": \"string\",\"regionId\": 0, \"address\": \"string\", \"mobile\": \"string\", \"message\": \"string\", \"advancesaleId\": 0, \"grouponId\": 0, \"point\": 0}";
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postOrder() throws Exception {
        String token = this.customerLogin("696371", "123456");
        String json = "{\"orderItems\": [\n" +
                "{\"productId\": 4303, \"onsaleId\": 2754,  \"quantity\": 2, \"couponActivityId\": 3, \"couponId\": 1}," +
                "{\"productId\": 4322, \"onsaleId\": 2773,  \"quantity\": 1, \"couponActivityId\": 3, \"couponId\": 1}," +
                "{\"productId\": 1776, \"onsaleId\": 227,  \"quantity\": 1}" +
                "  ], \"consignee\": \"李存维\",\"regionId\": 1636, \"address\": \"石井镇奎霞村北区2号\", \"mobile\": \"13959219573\", \"point\": 100}";
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
                //1583*2+13297*1+6954-100(point) + 1300(expressFee) - 1646（discountPrice）
                .jsonPath("$.data[0].amount").isEqualTo(22871)
                .returnResult().getResponseBody()), "UTF-8");

        orderRet1 = JacksonUtil.parseSubnodeToString(ret, "/data");
        /*   freight model 1 1636 泉州南安 - 14 福建省
                     model 2 1636 泉州南安 - 1 中国
                              4303  4322   1776
               weight         24    500    45
               onsaleid       2754  2773   227
               price          1583  13297 6954
               quantity       2      1      1
               model          2      1      1
               total      24*2+500+45=593
               model 1 ExpressFee   1000+100 = 1100
               model 2 （1）1000 + （2,3）100+ （4）100 = 1200

        优惠活动3 2件9折  1583*2+13297 = 16463 *0.1 = 1646
        */

    }

    /**
     * 微信支付
     * 看谁手快
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void payOrder1() throws Exception {
        assertNotNull(orderRet1);
        String orderSn = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/orderSn", String.class);
        String beginTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/beginTime", String.class);
        String endTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/endTime", String.class);
        Integer amount = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/amount", Integer.class);
        assertEquals(22871, amount);

        String token = this.customerLogin("696371", "123456");

        String json = String.format(PAYFORMAT, 1, orderSn, 0, amount, beginTime, endTime);
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
     * 2570 -100
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void CustomerSelf2() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1001)
                .jsonPath("$.data.userName").isEqualTo("696371")
                .jsonPath("$.data.name").isEqualTo("李存维")
                .jsonPath("$.data.mobile").isEqualTo("13959219573")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(2470);
    }

    /**
     * 再看看自己优惠卷，错误计算在其他模块
     * 两张，一张已使用，一张失效
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void CustomerCoupon2() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(COUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '2')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(3);
    }

    /**
     * 再看看自己订单，该有了吧
     * 如果没有就使劲看，四次看不到，算你错
     * 原有4个订单
     * 1001用户
     *
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getOrder2() throws Exception {
        String token = this.customerLogin("696371", "123456");
        Integer orderNum = 4;
        int time = 0;
        List<String> ret = null;
        while (orderNum <= 4 && time < RETRYTIME) {
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
            if (orderNum <= 4){
                Thread.sleep(5000);
            }
            time++;
        }
        //无论分不分单，取父订单
        for (String data : ret) {
            Integer orderId = JacksonUtil.parseInteger(data, "id");
            Integer pid = JacksonUtil.parseInteger(data, "pid");
            if (!orderIds.contains(orderId) && 0 == pid) {
                orderId1 = orderId;
                assertEquals(1001, JacksonUtil.parseInteger(data, "customerId"));
                break;
            }
        }
    }

    /**
     * 看看父订单支付
     * 如果没有支付没成功，就使劲支付，到成功为止
     * 2261
     *
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getPayment() throws Exception {
        assertNotNull(orderId1);
        assertNotNull(orderRet1);
        String sn = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/orderSn", String.class);
        String beginTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/beginTime", String.class);
        String endTime = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/endTime", String.class);
        Integer amount = JacksonUtil.parseSubnodeToObject(orderRet1, "/0/amount", Integer.class);
        assertEquals(22871, amount);

        String token = this.customerLogin("696371", "123456");
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
            for (String payment : payments) {
                state = JacksonUtil.parseSubnodeToObject(payment, "/state", Integer.class);
                if (state == 1) {
                    //寻找是否有成功的支付
                    paymentId = JacksonUtil.parseSubnodeToObject(payment, "/id", Integer.class);
                    break;
                }
            }

            if (5 == state) {
                //支付失败再支付一次
                String json = String.format(PAYFORMAT, 1, sn, 0, amount, beginTime, endTime);
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
            } else if (0 == state) {
                //休眠3s
                Thread.sleep(5000);
                state = 5;
            }
            time++;
        }
    }

    /**
     * 再看看子订单，
     * 见证奇迹的时刻
     * 1001用户
     * 父子订单已分
     *
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getOrder3() throws Exception {
        assertNotNull(orderId1);
        String token = this.customerLogin("696371", "123456");

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
            if (101 == state) {
                Thread.sleep(5000);
            }
            times++;
        }

        //查询待收货的订单 顾客看见父订单，两个子订单 供3个订单
        String ret = new String(Objects.requireNonNull(this.mallClient.get().uri(ORDER + "?state=200")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .returnResult().getResponseBody()), "UTF8");

        List<String> orders = JacksonUtil.parseSubnodeToStringList(ret, "/data/list");
        for (String order : orders) {
            Integer orderId = JacksonUtil.parseInteger(order, "id");
            if (orderId == orderId1) {
                //父订单
                this.mallClient.get().uri(ID, orderId1)
                        .header("authorization", token)
                        .exchange()
                        .expectHeader()
                        .contentType("application/json;charset=UTF-8")
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                        .jsonPath("$.data.pid").isEqualTo(0)
                        //1583*2+13297*1+6954
                        .jsonPath("$.data.originPrice").isEqualTo(23417)
                        //1583*2+13297*1 * 0.1
                        .jsonPath("$.data.discountPrice").isEqualTo(1646)
                        .jsonPath("$.data.expressFee").isEqualTo(1200)
                        .jsonPath("$.data.point").isEqualTo(100)
                        .jsonPath("$.data.regionId").isEqualTo(1636)
                        .jsonPath("$.data.consignee").isEqualTo("李存维")
                        .jsonPath("$.data.mobile").isEqualTo("13959219573")
                        .jsonPath("$.data.address").isEqualTo("石井镇奎霞村北区2号")
                        .jsonPath("$.data.grouponId").isEqualTo(0)
                        .jsonPath("$.data.advancesaleId").isEqualTo(0)
                        .jsonPath("$.data.state").isEqualTo(201)
                        .jsonPath("$.data.orderItem.length()").isEqualTo(0);
                continue;
            }

            Integer shopId = JacksonUtil.parseInteger(order, "shopId");
            if (1 == shopId) {
                subOrderId1 = orderId;
                this.mallClient.get().uri(ID, orderId)
                        .header("authorization", token)
                        .exchange()
                        .expectHeader()
                        .contentType("application/json;charset=UTF-8")
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                        .jsonPath("$.data.shop.id").isEqualTo(1)
                        .jsonPath("$.data.pid").isEqualTo(orderId1)
                        //1583*2+13297*1
                        .jsonPath("$.data.originPrice").isEqualTo(16463)
                        //17093*0.1
                        .jsonPath("$.data.discountPrice").isEqualTo(1646)
                        .jsonPath("$.data.expressFee").isEqualTo(0)
                        .jsonPath("$.data.regionId").isEqualTo(1636)
                        .jsonPath("$.data.consignee").isEqualTo("李存维")
                        .jsonPath("$.data.mobile").isEqualTo("13959219573")
                        .jsonPath("$.data.address").isEqualTo("石井镇奎霞村北区2号")
                        .jsonPath("$.data.grouponId").isEqualTo(0)
                        .jsonPath("$.data.advancesaleId").isEqualTo(0)
                        .jsonPath("$.data.state").isEqualTo(201)
                        .jsonPath("$.data.orderItem.length()").isEqualTo(2)
                        .jsonPath("$.data.orderItem[?(@.productId == '4303')].name").isEqualTo("安记粉蒸肉50")
                        .jsonPath("$.data.orderItem[?(@.productId == '4303')].quantity").isEqualTo(2)
                        .jsonPath("$.data.orderItem[?(@.productId == '4303')].price").isEqualTo(1583)
                        // 1/10分
                        .jsonPath("$.data.orderItem[?(@.productId == '4303')].discountPrice").isEqualTo(1583)
                        // 算给邮费了
                        .jsonPath("$.data.orderItem[?(@.productId == '4303')].point").isEqualTo(0)
                        .jsonPath("$.data.orderItem[?(@.productId == '4322')].name").isEqualTo("绍兴料酒")
                        .jsonPath("$.data.orderItem[?(@.productId == '4322')].quantity").isEqualTo(1)
                        .jsonPath("$.data.orderItem[?(@.productId == '4322')].price").isEqualTo(13297)
                        // 1/10分
                        .jsonPath("$.data.orderItem[?(@.productId == '4322')].discountPrice").isEqualTo(13297)
                        // 算给邮费了
                        .jsonPath("$.data.orderItem[?(@.productId == '4322')].point").isEqualTo(0);
            } else if (2 == shopId) {
                subOrderId2 = orderId;

                this.mallClient.get().uri(ID, orderId)
                        .header("authorization", token)
                        .exchange()
                        .expectHeader()
                        .contentType("application/json;charset=UTF-8")
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                        .jsonPath("$.data.shop.id").isEqualTo(2)
                        .jsonPath("$.data.pid").isEqualTo(orderId1)
                        .jsonPath("$.data.originPrice").isEqualTo(6954)
                        .jsonPath("$.data.discountPrice").isEqualTo(0)
                        .jsonPath("$.data.expressFee").isEqualTo(0)
                        .jsonPath("$.data.regionId").isEqualTo(1636)
                        .jsonPath("$.data.consignee").isEqualTo("李存维")
                        .jsonPath("$.data.mobile").isEqualTo("13959219573")
                        .jsonPath("$.data.address").isEqualTo("石井镇奎霞村北区2号")
                        .jsonPath("$.data.grouponId").isEqualTo(0)
                        .jsonPath("$.data.advancesaleId").isEqualTo(0)
                        .jsonPath("$.data.state").isEqualTo(201)
                        .jsonPath("$.data.orderItem.length()").isEqualTo(1)
                        .jsonPath("$.data.orderItem[?(@.productId == '1776')].name").isEqualTo("45王守义十三香")
                        .jsonPath("$.data.orderItem[?(@.productId == '1776')].quantity").isEqualTo(1)
                        .jsonPath("$.data.orderItem[?(@.productId == '1776')].price").isEqualTo(6954)
                        .jsonPath("$.data.orderItem[?(@.productId == '1776')].discountPrice").isEqualTo(0)
                        // 算给邮费了
                        .jsonPath("$.data.orderItem[?(@.productId == '1776')].point").isEqualTo(0);
            }
        }
    }

    /**
     * 顾客取消
     *
     * @throws Exception
     */
    @Test
    @Order(10)
    public void cancelOrder() throws Exception {
        assertNotNull(subOrderId1);
        String token = this.customerLogin("696371", "123456");
        this.mallClient.put().uri(CANCAL, subOrderId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
    }


    /**
     * 订单退款信息
     *
     * @throws Exception
     */
    @Test
    @Order(12)
    public void getRefund() throws Exception {
        assertNotNull(orderId1);
        assertNotNull(paymentId);
        Integer refundId = null;

        String token = this.customerLogin("696371", "123456");

        int state = 5;
        int time = 0;
        //支付失败试四次，四次都失败运气太差了，算你输
        while (5 == state && time < RETRYTIME) {
            String ret = new String(Objects.requireNonNull(this.gatewayClient.get().uri(REFUND, orderId1)
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");

            List<String> refunds = JacksonUtil.parseSubnodeToStringList(ret, "/data");
            state = 5;
            for (String refund : refunds) {
                state = JacksonUtil.parseSubnodeToObject(refund, "/state", Integer.class);
                if (state == 1) {
                    //寻找是否有成功的支付
                    refundId = JacksonUtil.parseSubnodeToObject(refund, "/id", Integer.class);
                    break;
                }
            }

            if (0 == state) {
                //休眠3s
                Thread.sleep(5000);
                state = 5;
            }
            time++;
        }

        this.gatewayClient.get().uri(REFUND, orderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data[0].id").isEqualTo(refundId)
                .jsonPath("$.data[0].paymentId").isEqualTo(paymentId)
                .jsonPath("$.data[0].amount").isEqualTo(22871)
                .jsonPath("$.data[0].state").isEqualTo(1);
    }

    /**
     * 取消后的订单
     *
     * @throws Exception
     */
    @Test
    @Order(13)
    public void getShopOrder2() throws Exception {
        assertNotNull(subOrderId2);
        assertNotNull(subOrderId1);
        String token = this.adminLogin("8131600001", "123456");

        //等待订单退款成功
        Integer state = 501;
        int times = 0;

        while (501 == state && times < RETRYTIME) {
            String ret = new String(Objects.requireNonNull(this.gatewayClient.get().uri(SHOPORDERID, 1, subOrderId1)
                    .header("authorization", token)
                    .exchange()
                    .expectHeader()
                    .contentType("application/json;charset=UTF-8")
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                    .returnResult().getResponseBody()), "UTF8");

            state = JacksonUtil.parseSubnodeToObject(ret, "/data/state", Integer.class);
            if (501 == state) {
                Thread.sleep(3000);
            }
            times++;
        }

        this.gatewayClient.get().uri(SHOPORDERID, 1, subOrderId1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(subOrderId1)
                .jsonPath("$.data.customer.id").isEqualTo(1001)
                .jsonPath("$.data.state").isEqualTo(502);

        token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(SHOPORDERID, 2, subOrderId2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(subOrderId2)
                .jsonPath("$.data.customer.id").isEqualTo(1001)
                .jsonPath("$.data.state").isEqualTo(502);
    }

    /**
     * 再看看自己多少点，错误计算在订单模块
     * 2570 -100
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void CustomerSelf3() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(SELF)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1001)
                .jsonPath("$.data.userName").isEqualTo("696371")
                .jsonPath("$.data.name").isEqualTo("李存维")
                .jsonPath("$.data.mobile").isEqualTo("13959219573")
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.point").isEqualTo(2570);
    }

    /**
     * 再看看自己优惠卷，错误计算在其他模块
     * 两张，一张已使用，一张失效
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void CustomerCoupon3() throws Exception {
        String token = this.customerLogin("696371", "123456");
        this.mallClient.get().uri(COUPON)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.id == '1')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '1')].state").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '2')].activityId").isEqualTo(3)
                .jsonPath("$.data.list[?(@.id == '2')].state").isEqualTo(3);
    }

}
