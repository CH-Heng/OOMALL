package cn.edu.xmu.oomall.activity.controller;

import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.activity.model.vo.GroupOnActivityVo;
import cn.edu.xmu.oomall.activity.model.vo.PageInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupOnActivityControllerTest {

    private static final InternalReturnObject getShopInfoRet1 = new InternalReturnObject(new SimpleShopVo(1L, "OOMALL自营商铺"));
    private static final InternalReturnObject getShopInfoRet2 = new InternalReturnObject(ReturnNo.INTERNAL_SERVER_ERR.getCode(), "外部API错误");

    private static final InternalReturnObject getOnsSlesOfProductRet1 = new InternalReturnObject(new PageInfoVo(Collections.singletonList(new SimpleOnSaleInfoVo(29L, 17931L, ZonedDateTime.parse("2021-11-11T14:38:20.000+08:00"),  ZonedDateTime.parse("2022-02-19T14:38:20.000+08:00"), 39L, 3L, null, (byte)2,null)), 1L, 1, 10, 1));
    private static final InternalReturnObject getOnsSlesOfProductRet2 = new InternalReturnObject(ReturnNo.INTERNAL_SERVER_ERR.getCode(), "外部API错误");

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShopService shopService;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private RedisUtil redisUtil;


    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = jwtHelper.createToken(1L,"admin",0L, 1,2000);

    @BeforeEach
    public void init() {
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
    }

    /**
     * 获得所有团购活动状态
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getGroupOnStatesTest() throws Exception {
        this.mvc.perform(get("/groupons/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("草稿"))
                .andExpect(jsonPath("$.data[1].code").value(1))
                .andExpect(jsonPath("$.data[1].name").value("上线"))
                .andExpect(jsonPath("$.data[2].code").value(2))
                .andExpect(jsonPath("$.data[2].name").value("下线"))
        ;
    }

    /**
     * 获得所有上线态的团购活动（正常流程）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getOnlineGroupOnActivitiesTest1() throws Exception {
        Mockito.when(goodsService.getOnSales(null, 1578L, null, null, 1, 10)).thenReturn(getOnsSlesOfProductRet1);
        this.mvc.perform(get("/groupons")
                .header("authorization", adminToken)
                .queryParam("shopId", "3")
                .queryParam("productId", "1578")
                .queryParam("beginTime", "2021-11-11T00:00:00.000+08:00")
                .queryParam("endTime", "2023-11-11T00:00:00.000+08:00"))
               .andExpect(status().isOk())
              //  .andExpect(jsonPath("$.errno").value(0))
//                .andExpect(jsonPath("$.data.page").value(1))
//                .andExpect(jsonPath("$.data.pageSize").value(10))
//                .andExpect(jsonPath("$.data.pages").value(1))
//                .andExpect(jsonPath("$.data.total").value(1))
//                .andExpect(jsonPath("$.data.list", hasSize(1)))
//                .andExpect(jsonPath("$.data.list[0].id").value(3))
//                .andExpect(jsonPath("$.data.list[0].name").value("团购活动3"))
        ;

    }

    /**
     * 获得上线态团购活动详情（正常流程）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getOnlineGroupOnActivityTest1() throws Exception {
        this.mvc.perform(get("/groupons/1")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.beginTime").value("2021-11-11T14:58:24.000+08:00"))
                .andExpect(jsonPath("$.data.endTime").value("2022-02-19T14:58:24.000+08:00"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("团购活动1"))
                //.andExpect(jsonPath("$.data.shopId").value(3))
                //.andExpect(jsonPath("$.data.strategy").isEmpty())
        ;
    }

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void getGroupOnActivitiesInShopTest1() throws Exception {
        this.mvc.perform(get("/shops/2/groupons")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.pages").value(1))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list", hasSize(2)))
                .andExpect(jsonPath("$.data.list[0].id").value(5))
                .andExpect(jsonPath("$.data.list[0].name").value("团购活动5"))
                .andExpect(jsonPath("$.data.list[1].id").value(7))
                .andExpect(jsonPath("$.data.list[1].name").value("团购活动7"))
        ;


    }

    /**
     * 管理员新增团购活动（正常流程）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addGroupOnActivityTest1() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0));
    }


    /**
     * 管理员查看特定团购活动详情（正常流程）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getGroupOnActivityInShopTest1() throws Exception {
        this.mvc.perform(get("/shops/3/groupons/1")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("团购活动1"))
                .andExpect(jsonPath("$.data.shopId").value(3))
//                .andExpect(jsonPath("$.data.strategy").value("[{\"quantity\":10,\"percentage\":10},{\"quantity\":20,\"percentage\":20},{\"quantity\":30,\"percentage\":30}]"))
                .andExpect(jsonPath("$.data.beginTime").value("2021-11-11T14:58:24.000+08:00"))
                .andExpect(jsonPath("$.data.endTime").value("2022-02-19T14:58:24.000+08:00"))
                .andExpect(jsonPath("$.data.gmtCreate").value("2021-11-11 14:58:24.000"))
                .andExpect(jsonPath("$.data.gmtModified").isEmpty())
                .andExpect(jsonPath("$.data.state").value(1))
                .andExpect(jsonPath("$.data.creator.id").value(1))
                .andExpect(jsonPath("$.data.creator.name").value("admin"))
                .andExpect(jsonPath("$.data.modifier.id").isEmpty())
                .andExpect(jsonPath("$.data.modifier.name").isEmpty())
                ;
    }

    /**
     * 日期格式不正确，得到bad request
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void invalidDateTimeFormatTest() throws Exception {
        this.mvc.perform(get("/groupons")
                .header("authorization", adminToken)
                .queryParam("shopId", "1")
                .queryParam("beginTime", "2021-11-11 00:00:00")
                .queryParam("endTime", "2023-11-11 00:00:00"))
                .andExpect(status().isBadRequest());

        this.mvc.perform(get("/shops/2/groupons")
                .header("authorization", adminToken)
                .queryParam("beginTime", "2021-11-11 00:00:00"))
                .andExpect(status().isBadRequest());

        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11 00:00:00\",\"endTime\":\"2021-11-13 00:00:00.000\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    /**
     * 获得上线态团购活动详情（ID未找到）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getOnlineGroupOnActivityTest2() throws Exception {
        this.mvc.perform(get("/groupons/0")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errno").value(504));
    }

    /**
     * 获得上线态团购活动详情（未上线）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getOnlineGroupOnActivityTest3() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        var responseString = this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var id = JacksonUtil.parseObject(responseString, "data", GroupOnActivityVo.class).getId();
        this.mvc.perform(get("/groupons/" + id)
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(507));
    }

    /**
     * 管理员查看特定团购活动详情（ID未找到）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getGroupOnActivityInShopTest2() throws Exception {
        this.mvc.perform(get("/shops/1/groupons/0")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errno").value(504));
    }


    /**
     * 管理员查看特定团购活动详情（未在该店铺中）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getGroupOnActivityInShopTest3() throws Exception {
        this.mvc.perform(get("/shops/1/groupons/1")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errno").value(504));
    }

    /**
     * 管理员新增团购活动（body格式不合法）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addGroupOnActivityTest2() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errno").value(503));
//        this.mvc.perform(post("/shops/1/groupons")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8")
//                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11 00:00:00.000\",\"endTime\":\"2021-11-13 00:00:00.000\",\"strategy\":[{\"quantity\":10,\"percentage\":500000}]}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errno").value(503));
    }

    /**
     * 管理员新增团购活动（开始日期晚于结束日期）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addGroupOnActivityTest3() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet1);
        this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2022-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errno").value(947));
    }

    /**
     * 管理员新增团购活动（getShopInfo出错）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addGroupOnActivityTest4() throws Exception {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(getShopInfoRet2);
        this.mvc.perform(post("/shops/1/groupons")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"测试\",\"beginTime\":\"2021-11-11T00:00:00.000+08:00\",\"endTime\":\"2021-11-13T00:00:00.000+08:00\",\"strategy\":[{\"quantity\":10,\"percentage\":500}]}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errno").value(500));
    }

    /**
     * 管理员查询商铺的所有状态团购活动（getOnSalesOfProduct出错）
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getOnlineGroupOnActivitiesTest2() throws Exception {
        Mockito.when(goodsService.getOnSales(null, 1578L, null, null, 1, 10)).thenReturn(getOnsSlesOfProductRet2);

        this.mvc.perform(get("/groupons")
                .header("authorization", adminToken)
                .queryParam("shopId", "3")
                .queryParam("productId", "1578")
                .queryParam("beginTime", "2021-11-11T00:00:00.000+08:00")
                .queryParam("endTime", "2023-11-11T00:00:00.000+08:00"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errno").value(500));
    }

}
