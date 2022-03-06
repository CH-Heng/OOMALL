package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.shop.microservice.PaymentService;
import cn.edu.xmu.oomall.shop.microservice.PrivilegeService;
import cn.edu.xmu.oomall.shop.microservice.ReconciliationService;
import cn.edu.xmu.oomall.shop.microservice.vo.RefundDepositVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @Author: 蒋欣雨
 * @Sn: 22920192204219
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShopControllerTest {
    private static String adminToken = "0";
    private static String shopToken = "0";
    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ReconciliationService reconciliationService;

    @MockBean
    private PrivilegeService privilegeService;

    /**
     * 获取店铺所有状态
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllState() throws Exception {
        String responseString = this.mvc.perform(get("/shops/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"未审核\"},{\"code\":1,\"name\":\"下线\"},{\"code\":2,\"name\":\"上线\"},{\"code\":4,\"name\":\"关闭\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }
    /**
     * 获取店铺信息
     * @throws Exception
     */
    @Test
    @Transactional
    public void getSimpleShopById() throws Exception {
        String responseString = this.mvc.perform(get("/shops/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"id\":1,\"name\":\"OOMALL自营商铺\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);

    }
    /**
     * 获取所有店铺信息
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllShop() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString = this.mvc.perform(get("/shops/0/shops?page=1&pageSize=3").contentType("application/json;charset=UTF-8")
                        .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"data\":{\"total\":10,\"pages\":4,\"pageSize\":3,\"page\":1,\"list\":[{\"id\":1,\"name\":\"OOMALL自营商铺\",\"deposit\":5000000,\"depositThreshold\":1000000,\"state\":2,\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":null,\"name\":null},\"gmtCreated\":null,\"gmtModified\":null},{\"id\":2,\"name\":\"甜蜜之旅\",\"deposit\":5000000,\"depositThreshold\":1000000,\"state\":2,\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":null,\"name\":null},\"gmtCreated\":null,\"gmtModified\":null},{\"id\":3,\"name\":\"向往时刻\",\"deposit\":5000000,\"depositThreshold\":1000000,\"state\":2,\"creator\":{\"id\":1,\"name\":\"admin\"},\"modifier\":{\"id\":null,\"name\":null},\"gmtCreated\":null,\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }
    /**
     * 获取所有店铺信息,id不为0
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllShopIdErro() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String responseString = this.mvc.perform(get("/shops/1/shops").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected=" {\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 新建店铺（正常流程）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void applyShop() throws Exception {
        Mockito.when(privilegeService.addToDepart(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject(0,""));
        adminToken =jwtHelper.createToken(1L,"admin",-1L,1, 3600);
        String requestJson = "{\"name\": \"我的商铺\"}";
        String responseString = this.mvc.perform(post("/shops").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"name\":\"我的商铺\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 新建店铺（不传名称）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void applyShop_null() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",-1L,1, 3600);
        String requestJson = "{\"name\": \"\"}";
        String responseString = this.mvc.perform(post("/shops").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":503,\"errmsg\":\"商铺名不能为空;\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 新建店铺（名称是空格）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void applyShop_space() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",-1L,1, 3600);
        String requestJson = "{\"name\": \"  \"}";
        String responseString = this.mvc.perform(post("/shops").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":503,\"errmsg\":\"商铺名不能为空;\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 审核店铺
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void auditShop() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString = this.mvc.perform(put("/shops/0/newshops/8/audit").contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    public void auditShop_false() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String requestJson = "{\"conclusion\": false}";
        String responseString = this.mvc.perform(put("/shops/0/newshops/1/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 修改店铺
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void modifyShop() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"name\": \"修改后的名称\"}";
        String responseString = this.mvc.perform(put("/shops/1").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    public void modifyShop_null() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"name\": \"\"}";
        String responseString = this.mvc.perform(put("/shops/1").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":503,\"errmsg\":\"商铺名不能为空;\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 修改店铺(试图改id)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void modifyShop_ID() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"name\": \"修改后的店铺名称\",\"id\":\"123\"}";
        String responseString = this.mvc.perform(put("/shops/1").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 上架店铺
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void onAndOffshelfShop() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/8/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        String responseString_onself = this.mvc.perform(put("/shops/7/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_onself, false);

        String responseString_offself = this.mvc.perform(put("/shops/1/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_offself, false);
    }


    /**
     * 关闭店铺，已清算完毕已打款
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteShop() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/8/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType(Byte.valueOf((byte) 0));
        refundDepositVo.setName("测试");

        Mockito.when(reconciliationService.isClean(7L)).thenReturn(new InternalReturnObject(true));
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(new InternalReturnObject());
        String responseString_delete = this.mvc.perform(delete("/shops/7").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_delete, false);
    }

    /**
     * 关闭店铺,已清算但状态不对
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteShop_online() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/8/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        String responseString_onself = this.mvc.perform(put("/shops/7/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_onself, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType(Byte.valueOf((byte) 0));
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(Long.valueOf(1))).thenReturn(new InternalReturnObject(true));
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(new InternalReturnObject());
        String responseString = this.mvc.perform(delete("/shops/1").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 关闭店铺,未清算
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteShop_isNotSettled() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/1/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType((byte) 0);
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(7L)).thenReturn(new InternalReturnObject(false));
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(new InternalReturnObject());
        String responseString = this.mvc.perform(delete("/shops/7").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);
    }

    /**
     * 关闭店铺,未打款
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteShop_isNotPay() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/1/audit").contentType("application/json;charset=UTF-8").header("authorization", adminToken).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType(Byte.valueOf((byte) 0));
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(6L)).thenReturn(new InternalReturnObject(false));
        InternalReturnObject returnObject = new InternalReturnObject(Integer.valueOf(ReturnNo.AUTH_INVALID_JWT.getCode()), "错了");
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(returnObject);
        String responseString = this.mvc.perform(delete("/shops/6").contentType("application/json;charset=UTF-8").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    @Transactional
    public void modifyShopForbid() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/10/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);
        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType(Byte.valueOf((byte) 0));
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(5L)).thenReturn(new InternalReturnObject(true));
        InternalReturnObject returnObject = new InternalReturnObject();
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(returnObject);
        String responseString_delete = this.mvc.perform(delete("/shops/5").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_delete, false);

//        String requestJson2 = "{\"name\": \"修改后\"}";
//        String responseString = this.mvc.perform(put("/shops/1").header("authorization", shopToken).contentType("application/json;charset=UTF-8").content(requestJson2))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        expected = "{\"errno\":507,\"errmsg\":\"商铺处于关闭态\"}";
//        JSONAssert.assertEquals(expected, responseString, false);
    }


    /**
     * 审核店铺
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void auditShop_forbid() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/1/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType((byte) 0);
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(7L)).thenReturn(new InternalReturnObject(true));
        InternalReturnObject returnObject = new InternalReturnObject();
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(returnObject);
        String responseString_delete = this.mvc.perform(delete("/shops/7").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_delete, false);


        String requestJson2 = "{\"conclusion\": true}";
        String responseString = this.mvc.perform(put("/shops/0/newshops/1/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }
//

    @Test
    @Transactional
    public void onshelfShop_forbid() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/8/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType((byte) 0);
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(7L)).thenReturn(new InternalReturnObject(true));
        InternalReturnObject returnObject = new InternalReturnObject();
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(returnObject);
        String responseString_delete = this.mvc.perform(delete("/shops/7").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_delete, false);

        String responseString = this.mvc.perform(put("/shops/1/online").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }


    /**
     * 下架店铺
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void offshelfShop_forbid() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L,1, 3600);
        shopToken =jwtHelper.createToken(1L,"admin",1L,1, 3600);
        String requestJson = "{\"conclusion\": true}";
        String responseString_audit = this.mvc.perform(put("/shops/0/newshops/8/audit").header("authorization", adminToken).contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_audit, false);

        RefundDepositVo refundDepositVo = new RefundDepositVo();
        refundDepositVo.setAccount("11111111");
        refundDepositVo.setType((byte) 0);
        refundDepositVo.setName("测试");
        Mockito.when(reconciliationService.isClean(7L)).thenReturn(new InternalReturnObject(true));
        InternalReturnObject returnObject = new InternalReturnObject();
        Mockito.when(paymentService.refund(refundDepositVo)).thenReturn(returnObject);

        String responseString_delete = this.mvc.perform(delete("/shops/7").header("authorization", adminToken, shopToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString_delete, false);

        String responseString = this.mvc.perform(put("/shops/1/offline").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    /**
     * 获取所有店铺信息
     * @throws Exception
     */
    @Test
    @Transactional
    public void getShopTest() throws Exception {
        String responseString = this.mvc.perform(get("/internal/shops/all")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected="{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"name\":\"OOMALL自营商铺\"},{\"id\":2,\"name\":\"甜蜜之旅\"},{\"id\":3,\"name\":\"向往时刻\"},{\"id\":4,\"name\":\"努力向前\"},{\"id\":5,\"name\":\"坚持就是胜利\"},{\"id\":6,\"name\":\"一口气\"},{\"id\":7,\"name\":\"商铺7\"},{\"id\":8,\"name\":\"商铺8\"},{\"id\":9,\"name\":\"商铺9\"},{\"id\":10,\"name\":\"商铺10\"}]}";
        JSONAssert.assertEquals(expected, responseString, true);
    }
}
