package cn.edu.xmu.oomall.share;


import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.share.microservice.GoodsService;
import cn.edu.xmu.oomall.share.microservice.ActivityService;
import cn.edu.xmu.oomall.share.microservice.vo.*;
import cn.edu.xmu.oomall.share.model.vo.SimpleObjectVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@SpringBootTest(classes = ShareApplication.class)
public class ShareTest {
    private static JwtHelper jwtHelper = new JwtHelper();

    private static String shopAdminToken1 = jwtHelper.createToken(1000050L, "admin", 100004L, 1, 3600);
    private static String shopAdminToken2 = jwtHelper.createToken(1000051L, "admin", 100001L, 1, 3600);
    private static String customerToken1 = jwtHelper.createToken(100001L, "wwk", -1L, -1, 3600);
    private static String customerToken2 = jwtHelper.createToken(13276L, "wwk", 0L, 1, 3600);
    private static String customerToken3 = jwtHelper.createToken(692L, "wwk", 0L, 1, 3600);
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private ActivityService activityService;

    @BeforeEach
    public void init() {
        OnsaleRetVo onsaleRetVo1=new OnsaleRetVo();
        onsaleRetVo1.setId(100003L);
        onsaleRetVo1.setShop(new SimpleObjectVo(100004L,"努力向前"));
        onsaleRetVo1.setProduct(new SimpleProductRetVo(100001552L,"",""));
        onsaleRetVo1.setShareActId(100004L);
        onsaleRetVo1.setShareActId(9L);
        Mockito.when(goodsService.selectFullOnsale(100003L)).thenReturn(new InternalReturnObject(0,"成功",onsaleRetVo1));

        OnsaleRetVo onsaleRetVo2=new OnsaleRetVo();
        onsaleRetVo1.setId(1000035L);
        onsaleRetVo1.setShop(new SimpleObjectVo(100004L,"努力向前"));
        onsaleRetVo1.setProduct(new SimpleProductRetVo(1552L,"",""));
        onsaleRetVo1.setShareActId(100004L);
        Mockito.when(goodsService.selectFullOnsale(1000035L)).thenReturn(new InternalReturnObject(0,"",onsaleRetVo2));


        ProductRetVo productRetVo1=new ProductRetVo();
        productRetVo1.setId(100001552L);
        productRetVo1.setShop(new SimpleObjectVo(100004L,"努力向前"));
        productRetVo1.setOnsaleId(100003L);
        Mockito.when(goodsService.getProductDetails(100001552L)).thenReturn(new ReturnObject(ReturnNo.OK,"",productRetVo1));

        ProductRetVo productRetVo2=new ProductRetVo();
        productRetVo2.setId(1551L);
        productRetVo2.setShop(new SimpleObjectVo(100004L,"努力向前"));
        productRetVo2.setOnsaleId(100003L);
        Mockito.when(goodsService.getProductDetails(1551L)).thenReturn(new ReturnObject(ReturnNo.OK,"",productRetVo2));

        List<StrategyVo> strategy=new ArrayList<>();
        strategy.add(new StrategyVo(1,1L));
        strategy.add(new StrategyVo(3,2L));
        strategy.add(new StrategyVo(10,10L));
        ShareActivityRetVo shareActivityRetVo =new ShareActivityRetVo();
        shareActivityRetVo.setId(100004L);
        shareActivityRetVo.setBeginTime(LocalDateTime.of(2021,11,11,15,01,23).atZone(ZoneId.systemDefault()));
        shareActivityRetVo.setEndTime(LocalDateTime.of(2022,11,11,15,01,23).atZone(ZoneId.systemDefault()));
        shareActivityRetVo.setStrategy(strategy);
        Mockito.when(activityService.getShareActivityById(100004L)).thenReturn(new InternalReturnObject(0,"",shareActivityRetVo));

        ShareActivityRetVo shareActivityRetVo1 =new ShareActivityRetVo();
        shareActivityRetVo.setId(9L);
        shareActivityRetVo.setBeginTime(LocalDateTime.of(2021,11,11,15,01,23).atZone(ZoneId.systemDefault()));
        shareActivityRetVo.setEndTime(LocalDateTime.of(2022,11,11,15,01,23).atZone(ZoneId.systemDefault()));
        shareActivityRetVo.setStrategy(strategy);
        Mockito.when(activityService.getShareActivityById(9L)).thenReturn(new InternalReturnObject(0,"",shareActivityRetVo1));
    }

    //分享者分享并生成链接
    @Test
    @Transactional
    public void testShare() throws Exception{
        String responseString = this.mvc.perform(post("/onsales/100003/shares")
                .header("authorization", customerToken1).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expected = "{\"errno\":0,\"data\":{\"sharer\":{\"id\":100001,\"name\":\"wwk\"},\"product\":{\"id\":1552,\"name\":\"\",\"imageUrl\":\"\"},\"quantity\":0,\"creator\":{\"id\":100001,\"name\":\"wwk\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    //买家查询分享记录
    @Test
    @Transactional
    public void testGetShares() throws Exception {
        String responseString = this.mvc.perform(get("/shares")
                .header("authorization", customerToken1)
                .contentType("application/json;charset=UTF-8")
                .param("productId","1000015528"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //用户查看商品的详细信息
    @Test
    @Transactional
    public void testGetShareProduct() throws Exception {
        String responseString = this.mvc.perform(get("/shares/24613/products/1551")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", customerToken1)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"data\":{\"id\":1551,\"shop\":{\"id\":100004,\"name\":\"努力向前\"},\"goodsId\":null,\"onsaleId\":100003,\"name\":null,\"skuSn\":null,\"imageUrl\":null,\"originalPrice\":null,\"weight\":null,\"price\":null,\"quantity\":null,\"state\":null,\"unit\":null,\"barCode\":null,\"originPlace\":null,\"category\":null,\"shareable\":null,\"freightId\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //商铺管理员查询本店商铺的商品分享记录
    @Test
    @Transactional
    public void testGetProductShares() throws Exception {
        String responseString = this.mvc.perform(get("/shops/100004/products/100001552/shares")
                .header("authorization", shopAdminToken1)
                .contentType("application/json;charset=UTF-8")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void testGetProductShares1() throws Exception {
        String responseString = this.mvc.perform(get("/shops/100001/products/100001552/shares")
                .header("authorization", shopAdminToken2)
                .contentType("application/json;charset=UTF-8")
        )
                .andExpect(status().is(403))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":505,\"errmsg\":\"该商品不属于该商铺\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //分享者查询所有分享成功记录
    @Test
    @Transactional
    public void testGetSuccessfulShares() throws Exception {
        String responseString = this.mvc.perform(get("/beshared")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", customerToken3)
        )
                .andExpect(status().isOk())
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //String expectString;
        //expectString = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectString, responseString, true);
    }

    //管理员查询商品的分享成功记录
    @Test
    @Transactional
    public void testGetProductSuccessfulShares() throws Exception {
        String responseString = this.mvc.perform(get("/shops/100004/products/100001552/beshared")
                .header("authorization", shopAdminToken1)
                .contentType("application/json;charset=UTF-8")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void testGetEarliestSuccessfulShares()throws Exception{
        String responseString = this.mvc.perform(get("/internal/share")
                .contentType("application/json;charset=UTF-8")
                .param("onsaleId","60")
                .param("customerId","6475")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":24575,\"sharerId\":12486,\"strategy\":null}}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    @Transactional
    public void testSetStateliquidated()throws Exception{
        String responseString = this.mvc.perform(put("/internal/beshared/800/liquidated")
                .contentType("application/json;charset=UTF-8")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString;
        expectString = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
