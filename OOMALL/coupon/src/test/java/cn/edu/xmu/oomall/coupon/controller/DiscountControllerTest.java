package cn.edu.xmu.oomall.coupon.controller;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.CouponApplication;
import cn.edu.xmu.oomall.coupon.microservice.GoodsService;
import cn.edu.xmu.oomall.coupon.microservice.vo.CategoryVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/11 17:06
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = CouponApplication.class)
public class DiscountControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);

    @Autowired
    private MockMvc mvc;
    @MockBean
    private GoodsService goodsService;
    @MockBean
    private RedisUtil redisUtil;

    CategoryVo category1=new CategoryVo(208L,null);
    ProductRetVo productRetVo1=new ProductRetVo(4322L,null,null,2773L,null,null,null,null,null,null,null,null,null,null,null,category1,null,null);

    CategoryVo category2=new CategoryVo(261L,null);
    ProductRetVo productRetVo2=new ProductRetVo(4303L,null,null,2754L,null,null,null,null,null,null,null,null,null,null,null,category2,null,null);

    @Test
    @Transactional
    public void calculateDiscount() throws Exception {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(goodsService.getProductById(4322L)).thenReturn(new InternalReturnObject<>(productRetVo1));
        Mockito.when(goodsService.getProductById(4303L)).thenReturn(new InternalReturnObject<>(productRetVo2));

        String requestJson="[{\"productId\": 4322,\"onsaleId\": 2773,\"quantity\": 1,\"originalPrice\": 132970,\"activityId\": 3}," +
                "{\"productId\": 4303,\"onsaleId\": 2754,\"quantity\": 5,\"originalPrice\": 15830,\"activityId\": 3}]";
        String responseJson=this.mvc.perform(put("/internal/discountprices")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString();
        String expectedJson="{\"errno\":0,\"data\":[{\"productId\":4322,\"onsaleId\":2773,\"discountPrice\":119673,\"activityId\":3},{\"productId\":4303,\"onsaleId\":2754,\"discountPrice\":14247,\"activityId\":3}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedJson, responseJson, false);
    }
}
