package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.microservice.AlipayService;
import cn.edu.xmu.oomall.payment.microservice.WechatPayService;
import cn.edu.xmu.oomall.payment.microservice.util.WarpRetObject;
import cn.edu.xmu.oomall.payment.microservice.util.WeChatPayReturnObject;
import cn.edu.xmu.oomall.payment.microservice.vo.DownloadUrlQueryRetVo;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayFundFlowBillRetVo;
import cn.edu.xmu.oomall.payment.model.bo.flowbill.AliPayFlowBillItem;
import cn.edu.xmu.oomall.payment.model.bo.flowbill.WeChatFlowBillItem;
import cn.edu.xmu.oomall.payment.model.vo.ErrorPaymentUpdateVo;
import cn.edu.xmu.oomall.payment.service.ReconciliationService;
import cn.edu.xmu.oomall.payment.util.FileCommon;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/8 11:25
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = PaymentApplication.class)
public class ReconciliationControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AlipayService alipayService;
    @MockBean
    private WechatPayService wechatPayService;

    String path="src/resources/fundflowbills/";

    @Test
    @Transactional
    public void getErrorPayment()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/erroraccounts?documentId=1234567890&state=0&beginTime=2020-12-01T17:00:00.000+08:00&endTime=2023-12-03T18:00:00.000+08:00&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"tradeSn\":\"1234567891234567890\",\"patternId\":1,\"income\":10000,\"expenditure\":0,\"state\":0,\"documentId\":null,\"time\":\"2021-12-03T17:55:12.000+08:00\"}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPayment_beginLaterEnd()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/erroraccounts?documentId&state=0&beginTime=2021-12-12T18:00:00.000+08:00&endTime=2021-12-03T18:00:00.000+08:00&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPayment_wrongState()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/erroraccounts?documentId&state=3&beginTime&endTime&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPayment_wrongShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/erroraccounts?documentId&state&beginTime&endTime&page=1&pageSize=10")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPaymentById()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/erroraccounts/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"id\":1,\"tradeSn\":\"1234567891234567890\",\"patternId\":1,\"income\":10000,\"expenditure\":0,\"documentId\":\"1234567890\",\"state\":0,\"time\":\"2021-12-03T17:55:12.000+08:00\",\"descr\":null,\"adjustTime\":\"2021-12-03T17:55:12.000+08:00\",\"gmtCreate\":\"2021-12-03T17:50:43.000+08:00\",\"gmtModified\":\"2021-12-03T17:50:45.000+08:00\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPaymentById_wrongShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/erroraccounts/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void getErrorPaymentById_noResource()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/0/erroraccounts/1000")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void updateErrorPayment()throws Exception{
        ErrorPaymentUpdateVo errorPaymentUpdateVo =new ErrorPaymentUpdateVo();
        errorPaymentUpdateVo.setState((byte)1);
        errorPaymentUpdateVo.setDescr("备注");
        String responseJson=this.mvc.perform(put("/shops/0/erroraccounts/1")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(errorPaymentUpdateVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"tradeSn\":\"1234567891234567890\",\"patternId\":1,\"income\":10000,\"expenditure\":0,\"documentId\":\"1234567890\",\"state\":1,\"time\":\"2021-12-03T17:55:12.000+08:00\",\"descr\":\"备注\",\"adjustTime\":\"2021-12-03T17:55:12.000+08:00\",\"gmtCreate\":\"2021-12-03T17:50:43.000+08:00\",\"adjust\":{\"id\":1,\"name\":\"admin\"},\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void updateErrorPayment_wrongState()throws Exception{
        ErrorPaymentUpdateVo errorPaymentUpdateVo =new ErrorPaymentUpdateVo();
        errorPaymentUpdateVo.setState((byte)1);
        errorPaymentUpdateVo.setDescr("备注");
        String responseJson=this.mvc.perform(put("/shops/0/erroraccounts/2")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(errorPaymentUpdateVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void updateErrorPayment_invalid()throws Exception{
        ErrorPaymentUpdateVo errorPaymentUpdateVo =new ErrorPaymentUpdateVo();
        errorPaymentUpdateVo.setState((byte)2);
        errorPaymentUpdateVo.setDescr("备注");
        String responseJson=this.mvc.perform(put("/shops/0/erroraccounts/2")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(errorPaymentUpdateVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":503,\"errmsg\":\"must be less than or equal to 1;\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void updateErrorPayment_noResource()throws Exception{
        ErrorPaymentUpdateVo errorPaymentUpdateVo =new ErrorPaymentUpdateVo();
        errorPaymentUpdateVo.setState((byte)1);
        errorPaymentUpdateVo.setDescr("备注");
        String responseJson=this.mvc.perform(put("/shops/0/erroraccounts/100")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(errorPaymentUpdateVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void updateErrorPayment_wrongShopId()throws Exception{
        ErrorPaymentUpdateVo errorPaymentUpdateVo =new ErrorPaymentUpdateVo();
        errorPaymentUpdateVo.setState((byte)1);
        errorPaymentUpdateVo.setDescr("备注");
        String responseJson=this.mvc.perform(put("/shops/1/erroraccounts/2")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(errorPaymentUpdateVo)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void reconciliation()throws Exception{
        Mockito.when(wechatPayService.getFundFlowBill("")).thenReturn(new InternalReturnObject<>(new WeChatPayReturnObject<>(new WeChatPayFundFlowBillRetVo())));
        Mockito.when(alipayService.gatewayDo(null, "alipay.data.dataservice.bill.downloadurl.query", null, "utf-8",
                "RSA2", null, null, null, "{\"bill_date\":\"\"}"))
                .thenReturn(new InternalReturnObject<>(new WarpRetObject()));

        String responseJson=this.mvc.perform(get("/shops/0/reconciliation?beginTime=2021-02-11T00:00:00.000+08:00&endTime=2021-02-11T00:00:00.000+08:00")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":{\"success\":3,\"error\":2,\"extra\":52},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }

    @Test
    @Transactional
    public void reconciliation_wrongShopId()throws Exception{
        String responseJson=this.mvc.perform(get("/shops/1/reconciliation?beginTime&endTime")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }


}
