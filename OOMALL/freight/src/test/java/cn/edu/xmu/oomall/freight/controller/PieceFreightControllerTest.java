package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PieceFreightControllerTest {
    @Autowired
    private MockMvc mvc;
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken;
    @MockBean
    private RedisUtil redisUtil;
    /**
     * 管理员成功定义件数模板明细
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(redisUtil.get(Mockito.any())).thenReturn(null);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(12L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"regionId\":12,\"firstItems\":1,\"firstItemFreight\":10,\"additionalItems\":1,\"additionalItemsPrice\":5,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }

    /**
     * 运费模板中该地区已经定义
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":997,\"errmsg\":\"该运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 该运费模板类型与内容不符
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/1/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":996,\"errmsg\":\"该运费模板类型与内容不符\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * shopId不为0
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/1/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * FreightModel的id不存在
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest4() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/6/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 管理员定义件数模板明细，传入参数不合法
     * @throws Exception
     */
    @Test
    public void createPieceFreightModelTest5() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
        Mockito.when(redisUtil.get(Mockito.any())).thenReturn(null);
        PieceFreightVo vo = new PieceFreightVo();
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(post("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":503}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 店家或管理员查询件数运费模板的明细
     * @throws Exception
     */
    @Test
    public void getPieceFreightTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(get("/shops/0/freightmodels/2/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"regionId\":152,\"firstItems\":2,\"firstItemFreight\":500,\"additionalItems\":2,\"additionalItemsPrice\":100,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-17T09:18:26\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":3,\"regionId\":1,\"firstItems\":1,\"firstItemFreight\":10,\"additionalItems\":2,\"additionalItemsPrice\":5,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-22T14:47:21\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":4,\"regionId\":2,\"firstItems\":1,\"firstItemFreight\":10,\"additionalItems\":3,\"additionalItemsPrice\":4,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-22T14:47:21\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 店家或管理员查询件数运费模板的明细,操作资源id不存在
     * @throws Exception
     */
    @Test
    public void getPieceFreightTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(get("/shops/0/freightmodels/6/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 店家或管理员查询件数运费模板的明细,模板id不存在
     * @throws Exception
     */
    @Test
    public void getPieceFreightTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(get("/shops/0/freightmodels/1/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 店家或管理员查询件数运费模板的明细,查不到
     * @throws Exception
     */
    @Test
    public void getPieceFreightTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(get("/shops/0/freightmodels/3/pieceItems").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    /**
     * 店家或管理员删掉件数运费模板明细
     * @throws Exception
     */
    @Test
    public void deletePieceFreightTest() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(delete("/shops/0/pieceItems/3").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 店家或管理员删掉件数运费模板明细,shopId不为0
     * @throws Exception
     */
    @Test
    public void deletePieceFreightTest1() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(delete("/shops/1/pieceItems/3").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 店家或管理员删掉件数运费模板明细，操作资源id不存在
     * @throws Exception
     */
    @Test
    public void deletePieceFreightTest2() throws Exception
    {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        String responseString = this.mvc.perform(delete("/shops/0/pieceItems/8").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 修改件数运费模板明细
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/0/pieceItems/3").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 修改件数运费模板明细,shopId不为0
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest1() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/1/pieceItems/3").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 修改件数运费模板明细，操作资源id不存在
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest2() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/0/pieceItems/8").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 修改件数运费模板明细，地区已经定义
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest3() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        vo.setRegionId(2L);
        vo.setFirstItems(1);
        vo.setFirstItemFreight(10L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/0/pieceItems/3").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":999,\"errmsg\":\"运费模板中该地区已经定义\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 修改件数运费模板明细,传入参数不合法
     * @throws Exception
     */

    @Test
    public void modifyPieceFreightModelTest4() throws Exception {
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,2000);
        PieceFreightVo vo = new PieceFreightVo();
        String requestJSON = JacksonUtil.toJson(vo);
        String responseString = this.mvc.perform(put("/shops/0/pieceItems/3").contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken).content(requestJSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":503}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

    }


}
