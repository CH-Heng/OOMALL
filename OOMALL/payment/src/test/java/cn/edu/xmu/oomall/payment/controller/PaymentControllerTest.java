package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.microservice.AlipayService;
import cn.edu.xmu.oomall.payment.microservice.WechatPayService;
import cn.edu.xmu.oomall.payment.microservice.bo.NotifyBody;
import cn.edu.xmu.oomall.payment.microservice.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.payment.microservice.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.payment.microservice.vo.*;
import cn.edu.xmu.oomall.payment.model.bo.DocumentType;
import cn.edu.xmu.oomall.payment.model.vo.ModifyVo;
import cn.edu.xmu.oomall.payment.model.vo.PaymentPatternVo;
import cn.edu.xmu.oomall.payment.model.vo.PaymentVo;
import cn.edu.xmu.oomall.payment.model.vo.RefundVo;
import cn.edu.xmu.oomall.payment.service.mq.CallBackListener;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 17:21
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = PaymentApplication.class)
public class PaymentControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, Integer.MAX_VALUE);

    private static final String WECHATPAY_TRADE_STATE_SUCCESS = "TRANSACTION.SUCCESS";
    private static final String ALIPAY_TRADE_STATE_SUCCESS = "TRADE_SUCCESS";
    private static final Logger logger = LoggerFactory.getLogger(CallBackListener.class);
    @Autowired
    RocketMQTemplate rocketMQTemplate;
//    @MockBean
//    RocketMQTemplate rocketMQTemplate;

    @MockBean
    WechatPayService wechatPayService;

    @MockBean
    AlipayService alipayService;

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    public void getPaymentStates()throws Exception{
        String responseJson=this.mvc.perform(get("/payment/states")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"待支付\"},{\"code\":1,\"name\":\"已支付\"},{\"code\":2,\"name\":\"已对账\"},{\"code\":3,\"name\":\"已清算\"},{\"code\":4,\"name\":\"已取消\"},{\"code\":5,\"name\":\"失败\"}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getRefundStates()throws Exception{
        String responseJson=this.mvc.perform(get("/refund/states")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getPaymentPatternStates()throws Exception{
        String responseJson=this.mvc.perform(get("/paypatterns/states")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"可用\"},{\"code\":1,\"name\":\"不可用\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getValidPaymentPatterns()throws Exception{
        String responseJson=this.mvc.perform(get("/paypatterns")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllPaymentPatterns()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/paypatterns")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":[{\"id\":0,\"name\":\"alipay\",\"state\":null,\"beginTime\":null,\"endTime\":null,\"className\":\"alipay\"},{\"id\":1,\"name\":\"wechatpay\",\"state\":null,\"beginTime\":null,\"endTime\":null,\"className\":\"wechatpay\"}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllPaymentPatternsErrorShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/paypatterns")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllPayments()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/payment")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllPaymentsErrorShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/payment")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getPaymentDetail()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/payment/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"patternId\":1,\"tradeSn\":\"1234567891234567890\",\"documentId\":\"1234567890\",\"documentType\":0,\"descr\":null,\"amount\":20000,\"actualAmount\":null,\"payTime\":\"2021-12-03T18:16:56\",\"state\":1,\"beginTime\":\"2021-12-03T18:14:32\",\"endTime\":\"2021-12-03T18:29:32\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"adjustTime\":\"2021-12-03T18:18:13\",\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-12-03T18:16:56\",\"gmtModified\":\"2021-12-03T18:17:50\",\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getPaymentDetailErrorShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/payment/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getPaymentDetailNotFound()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/payment/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyPayment()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/0/payment/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"patternId\":1,\"tradeSn\":\"1234567891234567890\",\"documentId\":\"1234567890\",\"documentType\":0,\"descr\":\"测试描述\",\"amount\":20000,\"actualAmount\":null,\"payTime\":\"2021-12-03T18:16:56\",\"state\":0,\"beginTime\":\"2021-12-03T18:14:32\",\"endTime\":\"2021-12-03T18:29:32\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"adjustTime\":\"2021-12-03T18:18:13\",\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-12-03T18:16:56\",\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyPaymentNotFound()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/0/payment/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyPaymentErrorShopId()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/1/payment/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllRefunds()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"tradeSn\":\"9876543219876543210\",\"patternId\":1,\"paymentId\":null,\"amount\":20000,\"documentId\":\"1234567890\",\"documentType\":0,\"refundTime\":\"2021-12-11T22:11:34\",\"state\":0,\"descr\":\"测试描述\",\"adjustId\":1,\"adjustName\":\"admin\",\"adjustTime\":\"2021-12-11T22:12:09\",\"creatorId\":1,\"creatorName\":\"admin\",\"modifierId\":1,\"modifierName\":\"admin\",\"gmtCreate\":\"2021-12-11T22:12:21\",\"gmtModified\":\"2021-12-11T22:12:23\"},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getAllRefundsErrorShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getRefundDetail()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/refund/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"tradeSn\":\"9876543219876543210\",\"patternId\":1,\"amount\":20000,\"state\":0,\"documentId\":\"1234567890\",\"documentType\":0,\"descr\":\"测试描述\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"adjustTime\":\"2021-12-11T22:12:09\",\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-12-11T22:12:21\",\"gmtModified\":\"2021-12-11T22:12:23\",\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getRefundDetailErrorShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/refund/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getRefundDetailNotFound()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/refund/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyRefund()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/0/refund/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":1,\"tradeSn\":\"9876543219876543210\",\"patternId\":1,\"amount\":20000,\"state\":0,\"documentId\":\"1234567890\",\"documentType\":0,\"descr\":\"测试描述\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"adjustTime\":\"2021-12-11T22:12:09\",\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyRefundNotFound()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/0/refund/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void modifyRefundDetailErrorShopId()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/1/refund/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void newRefundWechatOrder()throws Exception{
        /**
         * 除了团购以外的情况, 只需要直接建立一个与payment
         * 的amount完全相同的refund即可, 发来的refundVo中的Amount是null
         */
        RefundVo refundVo = new RefundVo();
        refundVo.setDocumentId("1234567892");
        refundVo.setDocumentType(DocumentType.ORDER.getCode().byteValue());
        Mockito.when(wechatPayService.createRefund(Mockito.any())).thenReturn(new InternalReturnObject());
        String responseJson=this.mvc.perform(post("/internal/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(refundVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void newRefundWechatGroupon()throws Exception{
        /**
         * 除了团购以外的情况, 只需要直接建立一个与payment
         * 的amount完全相同的refund即可, 发来的refundVo中的Amount是null
         */
        RefundVo refundVo = new RefundVo();
        refundVo.setAmount(1000L);
        refundVo.setDocumentId("1234567893");
        refundVo.setDocumentType(DocumentType.ORDER.getCode().byteValue());
        Mockito.when(wechatPayService.createRefund(Mockito.any())).thenReturn(new InternalReturnObject());
        String responseJson=this.mvc.perform(post("/internal/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(refundVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void newRefundAlipayOrder()throws Exception{
        /**
         * 除了团购以外的情况, 只需要直接建立一个与payment
         * 的amount完全相同的refund即可, 发来的refundVo中的Amount是null
         */
        RefundVo refundVo = new RefundVo();
        refundVo.setDocumentId("1234567894");
        refundVo.setDocumentType(DocumentType.ORDER.getCode().byteValue());
        Mockito.when(alipayService.gatewayDo(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>());
        String responseJson=this.mvc.perform(post("/internal/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(refundVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void newRefundAlipayGroupon()throws Exception{
        /**
         * 除了团购以外的情况, 只需要直接建立一个与payment
         * 的amount完全相同的refund即可, 发来的refundVo中的Amount是null
         */
        RefundVo refundVo = new RefundVo();
        refundVo.setAmount(1000L);
        refundVo.setDocumentId("1234567895");
        refundVo.setDocumentType(DocumentType.ORDER.getCode().byteValue());
        Mockito.when(alipayService.gatewayDo(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>());
        String responseJson=this.mvc.perform(post("/internal/refund")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(refundVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }


    @Test
    @Transactional
    public void modifyRefundErrorShopId()throws Exception{
        ModifyVo modifyVo = new ModifyVo((byte)0,"测试描述");
        String responseJson=this.mvc.perform(put("/shops/1/refund/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(modifyVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void alipayPayment() throws Exception{
        Mockito.when(alipayService.gatewayDo(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentPatternVo paymentPatternVo = new PaymentPatternVo(0L);
        String responseJson=this.mvc.perform(put("/payments/3/pay")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentPatternVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":3,\"patternId\":0,\"documentId\":\"1234567891\",\"documentType\":0,\"descr\":\"测试支付\",\"amount\":20000,\"actualAmount\":10000,\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void wechatpayPayment() throws Exception{
        Mockito.when(wechatPayService.createTransaction(Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentPatternVo paymentPatternVo = new PaymentPatternVo(1L);
        String responseJson=this.mvc.perform(put("/payments/3/pay")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentPatternVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"id\":3,\"patternId\":1,\"documentId\":\"1234567891\",\"documentType\":0,\"descr\":\"测试支付\",\"amount\":20000,\"actualAmount\":10000,\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void alipayNotifyPayment()throws Exception{
        NotifyBody notifyBody = new NotifyBody(LocalDateTime.now(),"202112191123533WCA","TRADE_SUCCESS",null);
        String responseJson=this.mvc.perform(post("/alipay/notify")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(notifyBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void alipayNotifyRefund()throws Exception{
        NotifyBody notifyBody = new NotifyBody(LocalDateTime.now(),"9876543219876543211","TRADE_SUCCESS","12645789");
        notifyBody.setRefund_fee(10000L);
        String responseJson=this.mvc.perform(post("/alipay/notify")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(notifyBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void wechatNotifyPayment()throws Exception{
        WeChatPayTransaction weChatPayTransaction = new WeChatPayTransaction();
        weChatPayTransaction.setTradeState(WECHATPAY_TRADE_STATE_SUCCESS);
        weChatPayTransaction.setOutTradeNo("202112191123533WCA");
        WeChatPayPaymentNotifyRetVo weChatPayRefundNotifyRetVo = new WeChatPayPaymentNotifyRetVo(weChatPayTransaction);
        weChatPayRefundNotifyRetVo.setEventType(WECHATPAY_TRADE_STATE_SUCCESS);
        String responseJson=this.mvc.perform(post("/wechat/payment/notify")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayRefundNotifyRetVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void wechatpayNotifyRefund()throws Exception{
        WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo = new WeChatPayRefundNotifyRetVo();
        weChatPayRefundNotifyRetVo.setEventType(WECHATPAY_TRADE_STATE_SUCCESS);
        WeChatPayRefundNotifyRetVo.Resource resource = new WeChatPayRefundNotifyRetVo.Resource();
        WeChatPayRefundNotifyRetVo.Ciphertext ciphertext = new WeChatPayRefundNotifyRetVo.Ciphertext();
        ciphertext.setOutRefundNo("9876543219876543210");
        resource.setCiphertext(ciphertext);
        weChatPayRefundNotifyRetVo.setResource(resource);
        String responseJson=this.mvc.perform(post("/wechat/refund/notify")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayRefundNotifyRetVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void alipayPay() throws Exception{
        Mockito.when(alipayService.gatewayDo(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentVo paymentVo = new PaymentVo();
        paymentVo.setAmount(10000L);
        paymentVo.setDocumentId("12345678911");
        paymentVo.setDocumentType((byte)1);
        paymentVo.setPatternId(1L);
        String responseJson=this.mvc.perform(post("/payments")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentVo)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"patternId\":1,\"documentId\":\"12345678911\",\"documentType\":1,\"descr\":null,\"amount\":10000,\"actualAmount\":10000,\"state\":0,\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void wechatpayPay() throws Exception{
        Mockito.when(wechatPayService.createTransaction(Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentVo paymentVo = new PaymentVo();
        paymentVo.setAmount(10000L);
        paymentVo.setDocumentId("12345678911");
        paymentVo.setDocumentType((byte)0);
        paymentVo.setPatternId(1L);
        PaymentPatternVo paymentPatternVo = new PaymentPatternVo(1L);
        String responseJson=this.mvc.perform(post("/payments")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentVo)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"patternId\":1,\"documentId\":\"12345678911\",\"documentType\":0,\"descr\":null,\"amount\":10000,\"actualAmount\":10000,\"state\":0,\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void alipayUserPay() throws Exception{
        Mockito.when(alipayService.gatewayDo(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentVo paymentVo = new PaymentVo();
        paymentVo.setAmount(10000L);
        paymentVo.setDocumentId("12345678911");
        paymentVo.setDocumentType((byte)1);
        paymentVo.setPatternId(1L);
        String responseJson=this.mvc.perform(post("/internal/payments")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"patternId\":1,\"documentId\":\"12345678911\",\"documentType\":1,\"descr\":null,\"amount\":10000,\"actualAmount\":10000,\"state\":0,\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void wechatpayUserPay() throws Exception{
        Mockito.when(wechatPayService.createTransaction(Mockito.any())).thenReturn(new InternalReturnObject());
        PaymentVo paymentVo = new PaymentVo();
        paymentVo.setAmount(10000L);
        paymentVo.setDocumentId("12345678911");
        paymentVo.setDocumentType((byte)0);
        paymentVo.setPatternId(1L);
        PaymentPatternVo paymentPatternVo = new PaymentPatternVo(1L);
        String responseJson=this.mvc.perform(post("/internal/payments")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(paymentVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson= "{\"errno\":0,\"data\":{\"patternId\":1,\"documentId\":\"12345678911\",\"documentType\":0,\"descr\":null,\"amount\":10000,\"actualAmount\":10000,\"state\":0,\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }
}
