package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.oomall.wechatpay.WeChatPayApplication;
import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
@SpringBootTest(classes = WeChatPayApplication.class)
@AutoConfigureMockMvc
@Transactional
public class WeChatPayControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeChatPayNotifyService weChatPayNotifyService;

    @Test
    @Transactional
    public void createTransactionTest1() throws Exception{

        Mockito.when(weChatPayNotifyService.paymentNotify(Mockito.any())).thenReturn(null);

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setTimeExpire(LocalDateTime.now());
        vo.setAmount(new TransactionAmountVo(100,"CNY"));
        vo.setPayer(new PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString;
        String expectedResponse;

        for(int i=11; i<=18; i++){
            vo.setOutTradeNo(String.valueOf(i));
            responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"prepayId\":\"wx26112221580621e9b071c00d9e093b0000\"}}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }

    }

    @Test
    @Transactional
    public void createTransactionTest2() throws Exception {

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("1");
        vo.setAmount(new TransactionAmountVo(100,"CNY"));
        vo.setPayer(new PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"订单已支付\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createTransactionTest3() throws Exception {

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("");
        vo.setAmount(new TransactionAmountVo(100,"CNY"));
        vo.setPayer(new PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"参数错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getTransactionTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/pay/transactions/out-trade-no/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"appid\":\"wxd678efh567hg6787\",\"mchid\":\"1230000109\",\"outTradeNo\":\"1\",\"transactionId\":\"1\",\"tradeType\":\"JSAPI\",\"tradeState\":\"SUCCESS\",\"tradeStateDesc\":null,\"amount\":{\"total\":100,\"payerTotal\":100,\"currency\":\"CNY\",\"payerCurrency\":\"CNY\"},\"payer\":{\"openid\":\"oUpF8uMuAJO_M2pxb1Q9zNjWeS6o\"}}}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    @Transactional
    public void getTransactionTest2() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/pay/transactions/out-trade-no/0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void closeTransactionTest1() throws Exception {

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/out-trade-no/1/close"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void closeTransactionTest2() throws Exception {

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/out-trade-no/0/close"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createRefundTest1() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new RefundAmountVo(100,100,"CNY"));

        String responseString;
        String expectedResponse;

        for(int i=1; i<=8; i++){
            vo.setOutTradeNo(String.valueOf(i));
            vo.setOutRefundNo(String.valueOf(i));
            responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            expectedResponse = "{\"data\":{\"outRefundNo\":\""+i+"\",\"transactionId\":\"1217752501201407033233368018\",\"outTradeNo\":\""+i+"\",\"channel\":\"ORIGINAL\",\"userReceivedAccount\":\"招商银行信用卡0403\",\"amount\":{\"total\":null,\"refund\":100,\"payerTotal\":100,\"settlementRefund\":null,\"settlementTotal\":null,\"discountRefund\":null,\"currency\":null}}}";
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }

    }

    @Test
    @Transactional
    public void createRefundTest2() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new RefundAmountVo(120,100,"CNY"));
        vo.setOutTradeNo("1");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"退款金额错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void createRefundTest3() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new RefundAmountVo(100,100,"CNY"));
        vo.setOutTradeNo("9");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"对应支付单未成功支付\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void createRefundTest4() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new RefundAmountVo(100,100,"CNY"));
        vo.setOutTradeNo("");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"参数错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void getRefundTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/refund/domestic/refunds/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"data\":{\"refundId\":\"1\",\"outRefundNo\":\"10\",\"transactionId\":\"1217752501201407033233368018\",\"outTradeNo\":\"10\",\"channel\":\"ORIGINAL\",\"userReceivedAccount\":\"招商银行信用卡0403\",\"status\":\"SUCCESS\",\"amount\":{\"total\":null,\"refund\":100,\"payerTotal\":100,\"payerRefund\":null,\"settlementRefund\":null,\"settlementTotal\":null,\"discountRefund\":null,\"currency\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    @Transactional
    public void getRefundTest2() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/refund/domestic/refunds/0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getFundFlowBillTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/bill/fundflowbill").queryParam("bill_date","2021-12-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"hashType\":\"SHA1\",\"hashValue\":\"79bb0f45fc4c42234a918000b2668d689e2bde04\",\"downloadUrl\":\"https://api.mch.weixin.qq.com/v3/billdownload/file?token=xxx\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
