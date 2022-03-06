package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.model.vo.QuantityVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/29
 */
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = GoodsApplication.class)
public class QuantityTest {

    @Autowired
    protected WebApplicationContext wac;

    String adminToken;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private RedisUtil redisUtil;

    @BeforeEach
    public void init() {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);

        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
    }

    @Test
    public void testDecr() throws Exception {
        QuantityVo vo=new QuantityVo();
        vo.setQuantity(1);

        String s = JacksonUtil.toJson(vo);
        //正常
        String res=this.mvc.perform(put("/internal/shops/0/onsales/500/decr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();
        String expect = "{\"errno\":500}";
        JSONAssert.assertEquals(expect, res, false);

        //不存在OnSale
        res=this.mvc.perform(put("/internal/shops/0/onsales/99999/decr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        expect = "{\"errno\":504,\"errmsg\":\"不存在该价格浮动\"}";
        JSONAssert.assertEquals(expect, res, false);

        // 不是该商家的OnSale
        res=this.mvc.perform(put("/internal/shops/66/onsales/900/decr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();
        expect="{\"errno\":505,\"errmsg\":\"该价格浮动不属于该商铺\"}";
        JSONAssert.assertEquals(expect, res, false);


        // 不在销售时间
        res=this.mvc.perform(put("/internal/shops/1/onsales/900/decr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        expect="{\"errno\":905,\"errmsg\":\"货品不在有效的销售状态和时间\"}";
        JSONAssert.assertEquals(expect, res, false);

    }

    @Test
    public void testIncr() throws Exception {
        QuantityVo vo=new QuantityVo();
        vo.setQuantity(1);

        String s = JacksonUtil.toJson(vo);
        String res=this.mvc.perform(put("/internal/shops/0/onsales/500/incr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();
        String expect="{\"errno\":500}";
        JSONAssert.assertEquals(expect, res, false);
        //不存在OnSale
        res=this.mvc.perform(put("/internal/shops/0/onsales/99999/incr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        expect="{\"errno\":504,\"errmsg\":\"不存在该价格浮动\"}";
        JSONAssert.assertEquals(expect, res, false);

        // 不是该商家的OnSale
        res=this.mvc.perform(put("/internal/shops/66/onsales/900/incr")
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();
        expect="{\"errno\":505,\"errmsg\":\"该价格浮动不属于该商铺\"}";
        JSONAssert.assertEquals(expect, res, false);

    }
}
