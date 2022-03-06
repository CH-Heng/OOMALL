package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.oomall.freight.util.ValidList;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.FreightModel;
import cn.edu.xmu.oomall.freight.model.po.FreightModelPo;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
class FreightModelControllerTest {

    @Autowired
    private FreightModelPoMapper freightModelPoMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    private String token="0";

    private static JwtHelper jwtHelper = new JwtHelper();

    @Test
    void addFreightModel() throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        //以下是正常情况返回的
        FreightModelInfoVo freightModelInfo = new FreightModelInfoVo("modelname",666,(byte)0,(byte)0);
        String json = JacksonUtil.toJson(freightModelInfo);
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/0/freightmodels")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"data\":{\"name\":\"modelname\",\"type\":0,\"unit\":666,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        //body字段不合法
        FreightModelInfoVo freightModelInfo2 = new FreightModelInfoVo(null,666,(byte)0,(byte)0);
        String json2 = JacksonUtil.toJson(freightModelInfo2);
        String responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/0/freightmodels")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(json2))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\"errno\":503,\"errmsg\":\"模板名不能为空;\"}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);

        //shopid!=0
        String responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/66/freightmodels")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{" +
                "\"errno\": 505," +
                "\"errmsg\": \"操作的资源id不是自己的对象\"" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,true);
    }

    @Test
    void showFreightModel() throws Exception {
        //name字段存在
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels")
                .header("authorization",token)
                .param("name","freight model/100g").param("page","1").param("pageSize","10")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"freight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2020-12-02T20:33:08.000\",\"gmtModified\":\"2020-12-02T20:33:08.000\",\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //name字段不存在
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels")
                .header("authorization",token)
                .param("page","1").param("pageSize","10")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"name\":\"freight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2020-12-02T20:33:08.000\",\"gmtModified\":\"2020-12-02T20:33:08.000\",\"modifier\":{\"id\":null,\"name\":null}},{\"id\":2,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2020-12-02T20:33:08.000\",\"gmtModified\":\"2020-12-02T20:33:08.000\",\"modifier\":{\"id\":null,\"name\":null}},{\"id\":3,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-22T14:47:21.000\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);
    }

    @Test
    void cloneFreightModel() throws Exception {
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("freightModel_"+1L)).thenReturn(null);
        //以下是正常情况返回的,没过redis
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/0/freightmodels/1/clone")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\"errno\":0,\"data\":{\"type\":0,\"unit\":100,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        FreightModel freightModel=new FreightModel(2L,"piece model/2",(byte)0,(byte)1,2,1L,"admin",null,null,null,null);
        Mockito.when(redisUtil.get("freightModel_"+2L)).thenReturn(freightModel);
        //以下是正常情况返回的,过了redis
        String responseStringR;
        responseStringR = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/0/freightmodels/2/clone")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedStringR = "{\"errno\":0,\"data\":{\"type\":0,\"unit\":2,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedStringR,responseStringR,false);

        //非管理员返回错误
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/1/freightmodels/1/clone")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString1 = "{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString1,responseString1,false);

        //操作的资源id不存在
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/0/freightmodels/666/clone")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\n" +
                "\"errno\": 504,\n" +
                "\"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,false);
    }

    @Test
    void showDefaultFreightModel() throws Exception {
        //正常情况
        FreightModel freightModel=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel);
        //以下是正常情况返回的,过redis
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/default")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString="{\"errno\":0,\"data\":{\"id\":2,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":1,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString,responseString,true);

        //默认模板不存在返回404,errno:504
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/freightmodels/default")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(504));


        //正常情况
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(null);
        FreightModelPo freightModelPo=new FreightModelPo();
        freightModelPo.setGmtCreate(LocalDateTime.of(2021,11,20,2,51));
        freightModelPo.setDefaultModel((byte)1);
        freightModelPo.setName("piece model/3");
        freightModelPo.setType((byte)1);
        freightModelPo.setUnit(3);
        freightModelPo.setCreatorId(1L);
        freightModelPo.setCreatorName("admin");
        //原来没有默认模板，现在插入一个
        freightModelPoMapper.insertSelective(freightModelPo);
        //以下是正常情况返回的,没过redis,查数据库
        String responseString2= this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/default")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedResponseString2="{\"errno\":0,\"data\":{\"name\":\"piece model/3\",\"type\":1,\"unit\":3,\"defaultModel\":1,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-20T02:51:00.000\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString2,responseString2,false);
    }

    @Test
    void showFreightModelById() throws Exception {
        //正常情况
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        //以下是正常情况返回的,不过redis
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/2")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString2="{\"errno\":0,\"data\":{\"id\":2,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2020-12-02T20:33:08.000\",\"gmtModified\":\"2020-12-02T20:33:08.000\",\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString2,responseString2,true);


        //正常情况
        FreightModel freightModel=new FreightModel(2L,"piece model/2",(byte)0,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("freightModel_"+2L)).thenReturn(freightModel);
        //以下是正常情况返回的,过redis
        String responseString;
        responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/2")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString="{\"errno\":0,\"data\":{\"id\":2,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString,responseString,true);

        //查不到，返回默认模板
        FreightModel freightDefaultModel=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightDefaultModel);
        //以下是正常情况返回的,过redis
        String responseString3;
        responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/666666")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString3="{\"errno\":0,\"data\":{\"id\":2,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":1,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString3,responseString3,true);

        //查不到，查默认模板,默认模板再查不到,返回404
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(null);
        //以下是正常情况返回的,过redis
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/freightmodels/666666")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(504));

    }

    @Test
    void updateFreightModel() throws Exception {
        //body字段不合法
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        FreightModelInfoVo freightModelInfo0 = new FreightModelInfoVo("modelname",-1,(byte)1,(byte) 0);
        String json0 = JacksonUtil.toJson(freightModelInfo0);
        String responseString0= this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/freightmodels/1")
                .header("authorization",token)
                .content(json0)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString0 = "{\"errno\":503,\"errmsg\":\"最小值为0;\"}";
        JSONAssert.assertEquals(expectedString0,responseString0,true);


        FreightModelInfoVo freightModelInfo = new FreightModelInfoVo("modelname",666,(byte)1,(byte) 0);
        //如果更新的不是默认模板（默认模板id为2）
        FreightModel freightModel1=new FreightModel(2L,"piece model/2",(byte)0,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel1);
        String json = JacksonUtil.toJson(freightModelInfo);
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/freightmodels/1")
                .header("authorization",token)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString1="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString1,responseString1,true);

        //如果更新的是默认模板,此测试将修改id为1为默认模板,之前没有默认模板
        freightModelInfo.setDefaultModel((byte)1);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        json = JacksonUtil.toJson(freightModelInfo);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(null);
        String responseStringNoDefault;
        responseStringNoDefault = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/freightmodels/1")
                .header("authorization",token)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseStringNoDefault="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseStringNoDefault,responseStringNoDefault,true);

        //如果更新的是默认模板,此测试将修改id为1为默认模板,通过redis找到旧的模板
        freightModelInfo.setDefaultModel((byte)1);
        FreightModel freightModel2=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        json = JacksonUtil.toJson(freightModelInfo);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel2);
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/freightmodels/1")
                .header("authorization",token)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString2="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString2,responseString2,true);

        //如果非管理员
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        String responseString3;
        responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/4/freightmodels/2")
                .header("authorization",token)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedResponseString3="{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponseString3,responseString3,true);

        //如果更新的id不存在 404
        freightModelInfo.setDefaultModel((byte)0);
        FreightModel freightModel4=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        json = JacksonUtil.toJson(freightModelInfo);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel4);
        String responseString4;
        responseString4 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/freightmodels/66666666")
                .header("authorization",token)
                .content(json)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString4="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponseString4,responseString4,true);
    }

    @Test
    void deleteFreightModel() throws Exception {
        //正常情况
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(null);
        String responseString3;
        responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/0/freightmodels/1")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponseString3 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponseString3,responseString3,true);


        //如果删除的是默认模板
        FreightModel freightModel=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel);
        String responseString1;
        responseString1 = this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/0/freightmodels/2")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedResponseString1="{\"errno\":998,\"errmsg\":\"存在上架销售商品，不能删除运费模板\"}";
        JSONAssert.assertEquals(expectedResponseString1,responseString1,true);

        //非管理员
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        String responseString2;
        responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/1/freightmodels/2")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedResponseString2="{\n" +
                "\"errno\": 505,\n" +
                "\"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponseString2,responseString2,true);

        //如果删除的id不存在 404
        FreightModel freightModel4=new FreightModel(2L,"piece model/2",(byte)1,(byte)1,2,1L,"admin",null,null,null,null);
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);
        Mockito.when(redisUtil.get("defaultFrightModel")).thenReturn(freightModel4);
        String responseString4;
        responseString4 = this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/0/freightmodels/6666666")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedResponseString4="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponseString4,responseString4,true);
    }


    @Test
    @Transactional
    void calculateFreightTest1() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 1,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":200\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/151/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freightPrice").value(1000))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    @Transactional
    void calculateFreightTest2() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 30,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":200\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/151/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freightPrice").value(6500))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    @Transactional
    void calculateFreightTest3() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 300,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":20\n" +
                "  },\n" +
                "  {\n" +
                "    \"productId\":2,\n" +
                "    \"quantity\": 140,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":100\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/1604/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freightPrice").value(15500))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    @Transactional
    void calculateFreightTest4() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 300,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":20\n" +
                "  },\n" +
                "  {\n" +
                "    \"productId\":2,\n" +
                "    \"quantity\": 140,\n" +
                "    \"freightId\":2,\n" +
                "    \"weight\":100\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/1604/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freightPrice").value(15500))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    @Transactional
    void calculateFreightTest5() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 30,\n" +
                "    \"freightId\":2,\n" +
                "    \"weight\":200\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/414/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freightPrice").value(70))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    @Transactional
    void calculateFreightTest6() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": -30,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":200\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/151/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    @Transactional
    void calculateFreightTest7() throws Exception {
        String json = "[\n" +
                "  {\n" +
                "    \"productId\":1,\n" +
                "    \"quantity\": 30,\n" +
                "    \"freightId\":1,\n" +
                "    \"weight\":200\n" +
                "  }\n" +
                "]";
        this.mockMvc.perform(post("/regions/400/price")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void validListTest() throws Exception {
        ValidList<String> list = new ValidList<>();
        list.add("3");
        list.add(0, "0");
        list.addAll(1, List.of("1", "2"));
        list.addAll(List.of("4", "5", "6", "7"));
        list.removeIf(x -> Integer.parseInt(x) % 2 == 0);
        list.remove(0);
        list.remove("5");
        list.removeAll(List.of());
        Assert.isTrue(list.getList().equals(List.of("3", "7")), "");
        Assert.isTrue(!list.isEmpty(), "");
        Assert.isTrue(list.size() == 2, "");
        Assert.isTrue(list.contains("3"), "");
        Assert.isTrue(list.containsAll(List.of("3", "7")), "");
        String[] arr = {"3", "7"};
        Assert.isTrue(Arrays.equals(list.toArray(), arr), "");
        Assert.isTrue(Arrays.equals(list.toArray(new String[0]), arr), "");
        Assert.isTrue(list.get(0).equals("3"), "");
        Assert.isTrue(list.indexOf("3") == 0, "");
        Assert.isTrue(list.lastIndexOf("3") == 0, "");
        list.setList(list.subList(0, 2));
        list.set(1, "4");
        var it = list.listIterator();
        Assert.isTrue(it.next().equals("3"), "");
        it = list.listIterator(1);
        Assert.isTrue(it.next().equals("4"), "");
        list.retainAll(List.of("4"));
        list.clear();
        Assert.isTrue(list.equals(new ValidList<>()), "");
        Assert.isTrue(list.toString().equals("ValidList(list=[])"), list.toString());
    }
}
