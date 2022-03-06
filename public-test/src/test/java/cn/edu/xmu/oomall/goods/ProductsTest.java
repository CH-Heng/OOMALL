/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.goods.vo.ProductNewReturnVo;
import cn.edu.xmu.oomall.goods.vo.ProductVo;
import cn.edu.xmu.oomall.goods.vo.SimpleOnSaleRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsTest extends BaseTestOomall {

    private static final String STATEURL ="/goods/products/states";
    private static final String GETPRODUCT ="/goods/products";
    private static final String PRODUCTID ="/goods/products/{id}";
    private static final String DRAFT = "/goods/shops/{shopId}/draftproducts";
    private static final String DRAFTID = "/goods/shops/{shopId}/draftproducts/{id}";
    private static final String GOODSID ="/goods/goods/{id}";
    private static final String CATEGORYPRODUCT ="/goods/categories/{id}/products";
    private static final String PUBLISH = "/goods/shops/{shopId}/draftproducts/{id}/publish";
    private static final String ONSHELF = "/goods/shops/{shopId}/products/{id}/onshelves";
    private static final String OFFSHELF = "/goods/shops/{shopId}/products/{id}/offshelves";
    private static final String PROHIBIT = "/goods/shops/{shopId}/products/{id}/prohibit";
    private static final String ALLOW = "/goods/shops/{shopId}/products/{id}/allow";
    private static final String ONSALE = "/goods/shops/{shopId}/products/{id}/onsales";
    private static final String ONSALEID = "/goods/shops/{shopId}/onsales/{id}";
    private static final String ONSALEONLINE = "/goods//shops/{shopId}/onsales/{id}/online";
    private static final String ONSALEOFFLINE = "/goods//shops/{shopId}/onsales/{id}/offline";
    private static final String SHOPPRODUCTID = "/goods/shops/{shopId}/products/{id}";

    private static Long draftProdId1 = null;
    private static Long draftProdId2 = null;
    private static Long draftProdId3 = null;
    private static Long productId = null;
    private static Long onsaleId1 = null;
    private static Long onsaleId2 = null;
    /**
     * 获得product的所有状态
     * @throws Exception
     */
    @Test
    public void getProductState() throws Exception {
        this.mallClient.get().uri(STATEURL)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.length()").isEqualTo(4);
    }
    @Test
    public void getProduct1() throws Exception {
        this.mallClient.get().uri(GETPRODUCT+"?shopId=1&barcode=6922127400041&page=1&pageSize=100")
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='4893')].name").isEqualTo("赛亚八宝粥");
    }
    @Test
    public void getProduct2() throws Exception {
        this.mallClient.get().uri(GETPRODUCT+"?shopId=1&barcode=6922127405046&page=1&pageSize=100")
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='4901')].name").isEqualTo("赛亚顶上泰国香米5");
    }
    @Test
    public void getProductDetail1() throws Exception {
        this.mallClient.get().uri(PRODUCTID, 4893)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(4893)
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(3344)
                .jsonPath("$.data.name").isEqualTo("赛亚八宝粥")
                .jsonPath("$.data.originalPrice").isEqualTo(68434)
                .jsonPath("$.data.price").isEqualTo(14254)
                .jsonPath("$.data.quantity").isEqualTo(69);
    }
    /**
     * 不存在的id
     * @throws Exception
     */
    @Test
    public void getGoodsTest1() throws Exception {
        this.mallClient.get().uri(GOODSID, 912332)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 获得goods
     * @throws Exception
     */
    @Test
    @Order(0)
    public void getGoodsTest2() throws Exception {
        this.mallClient.get().uri(GOODSID, 92)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.productList.length()").isEqualTo(8);
    }
    /**
     * 不存在的id
     * @throws Exception
     */
    @Test
    public void getCateProductTest1() throws Exception {
        this.mallClient.get().uri(CATEGORYPRODUCT, 912332)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 获得category products
     *  @throws Exception
     */
    @Test
    @Order(0)
    public void getCateProductTest2() throws Exception {
        this.mallClient.get().uri(CATEGORYPRODUCT+"?page=1&pageSize=100", 257)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(41);
    }
    /**
     * 新增商铺1商品
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postDraftProductTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"skuSn\": \"test11\"," +
                "\"name\": \"测试商品1\"," +
                "\"originalPrice\": 100000," +
                "\"weight\": 1000," +
                "\"categoryId\": 257," +
                "  \"goodsId\": 92}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(DRAFT, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        ProductNewReturnVo vo = JacksonUtil.parseObject(ret, "data", ProductNewReturnVo.class);
        this.draftProdId1 = vo.getId();

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品1')].id").isEqualTo(this.draftProdId1.intValue())
                .jsonPath("$.data.list[?(@.name == '测试商品1')].productId").isEqualTo(0);
    }
    /**
     * 不存在的
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getProductDetail2() throws Exception {
        assertNotNull(this.draftProdId1);
        this.mallClient.get().uri(PRODUCTID, this.draftProdId1)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 获得goods
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getGoodsTest3() throws Exception {
        this.mallClient.get().uri(GOODSID, 92)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.productList.length()").isEqualTo(8);
    }
    /**
     * 获得category products
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getCateProductTest3() throws Exception {
        this.mallClient.get().uri(CATEGORYPRODUCT+"?page=1&pageSize=100", 257)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(41);
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    public void postDraftProductTest3() throws Exception{
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"skuSn\": \"test11\"," +
                "\"name\": \"测试商品1\"," +
                "\"originalPrice\": 100000," +
                "\"weight\": 1000," +
                "\"categoryId\": 257," +
                "  \"goodsId\": 92}";
        this.gatewayClient.post().uri(DRAFT, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 非同店铺
     * @throws Exception
     */
    @Test
    public void postDraftProductTest4() throws Exception{
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"skuSn\": \"test11\"," +
                "\"name\": \"测试商品1\"," +
                "\"originalPrice\": 100000," +
                "\"weight\": 1000," +
                "\"categoryId\": 257," +
                "  \"goodsId\": 92}";
        this.gatewayClient.post().uri(DRAFT, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void putDraftProductTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"skuSn\": \"2222222\"}";
        this.gatewayClient.put().uri(DRAFTID, 1, 78552246)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 未登录
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putDraftProductTest2() throws Exception{
        assertNotNull(this.draftProdId1);
        String json = "{\"skuSn\": \"2222222\"}";
        this.gatewayClient.put().uri(DRAFTID, 1, this.draftProdId1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putDraftProductTest3() throws Exception{
        assertNotNull(this.draftProdId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        String json = "{\"skuSn\": \"2222222\"}";
        this.gatewayClient.put().uri(DRAFTID, 1, this.draftProdId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 非同店铺
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putDraftProductTest4() throws Exception{
        assertNotNull(this.draftProdId1);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"skuSn\": \"2222222\"}";
        this.gatewayClient.put().uri(DRAFTID, 1, this.draftProdId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 修改商铺1商品
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putDraftProductTest5() throws Exception{
        assertNotNull(this.draftProdId1);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"skuSn\": \"2222222\"}";
        this.gatewayClient.put().uri(DRAFTID, 1, this.draftProdId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品1')].id").isEqualTo(this.draftProdId1.intValue())
                .jsonPath("$.data.list[?(@.name == '测试商品1')].skuSn").isEqualTo("2222222");
    }
    /**
     * 新增商铺2商品
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postDraftProductTest5() throws Exception{
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"skuSn\": \"test11\"," +
                "\"name\": \"测试商品2\"," +
                "\"originalPrice\": 200000," +
                "\"weight\": 21000," +
                "\"categoryId\": 257," +
                "  \"goodsId\": 92}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(DRAFT, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");

        ProductNewReturnVo vo = JacksonUtil.parseObject(ret, "data", ProductNewReturnVo.class);
        this.draftProdId2 = vo.getId();

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品2')].id").isEqualTo(this.draftProdId2.intValue());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delDraftProductTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(DRAFTID, 1, 5987456)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    @Order(5)
    public void delDraftProductTest2() throws Exception{
        assertNotNull(this.draftProdId2);
        this.gatewayClient.delete().uri(DRAFTID, 2, this.draftProdId2)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(5)
    public void delDraftProductTest3() throws Exception{
        assertNotNull(this.draftProdId2);
        String token = this.adminLogin("shop2_adv", "123456");
        this.gatewayClient.delete().uri(DRAFTID, 2, this.draftProdId2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 非同店铺
     * @throws Exception
     */
    @Test
    @Order(5)
    public void delDraftProductTest4() throws Exception{
        assertNotNull(this.draftProdId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(DRAFTID, 2, this.draftProdId2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 删除商铺2商品
     * @throws Exception
     */
    @Test
    @Order(6)
    public void delDraftProductTest5() throws Exception{
        assertNotNull(this.draftProdId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(DRAFTID, 2, this.draftProdId2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品2')]").doesNotExist();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    @Order(7)
    public void publishProductTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(PUBLISH, 0L, 598656)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    @Order(7)
    public void publishProductTest2() throws Exception{
        assertNotNull(this.draftProdId1);
        this.gatewayClient.put().uri(PUBLISH, 1, this.draftProdId1)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 店铺1管理员发布
     * @throws Exception
     */
    @Test
    @Order(7)
    public void publishProductTest5() throws Exception{
        assertNotNull(this.draftProdId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(PUBLISH, 1, this.draftProdId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品1')]").exists();
    }
    /**
     * 平台管理员发布
     * @throws Exception
     */
    @Test
    @Order(8)
    public void publishProductTest6() throws Exception{
        assertNotNull(this.draftProdId1);
        String token = this.adminLogin("13088admin", "123456");
        String ret = new String(Objects.requireNonNull(this.gatewayClient.put().uri(PUBLISH, 0, this.draftProdId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode()).returnResult().getResponseBody()), "UTF-8");

        ProductVo vo = JacksonUtil.parseObject(ret, "data", ProductVo.class);
        this.productId = vo.getId();

        this.gatewayClient.get().uri(DRAFT+"?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品1')]").doesNotExist();
    }
    /**
     * 未上架的商品，无销售
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getProductDetail3() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(1000);
    }
    /**
     * 获得goods
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getGoodsTest4() throws Exception {
        this.mallClient.get().uri(GOODSID, 92)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.productList.length()").isEqualTo(9);
    }
    /**
     * 获得category products
     * 未上架
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getCateProductTest4() throws Exception {
        this.mallClient.get().uri(CATEGORYPRODUCT+"?page=1&pageSize=100", 257)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(41);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postOnSaleTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 5000," +
                "\"beginTime\": \"2021-12-18T20:38:20.000+08:00\"," +
                "\"endTime\": \"2022-12-18T20:38:20.000+08:00\","+
                "  \"quantity\": 10000," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        this.gatewayClient.post().uri(ONSALE, 1, 1223345)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    @Order(9)
    public void postOnSaleTest2() throws Exception{
        assertNotNull(this.productId);
        String json = "{\"price\": 5000," +
                "\"beginTime\": \"2021-12-18T20:38:20.000+08:00\"," +
                "\"endTime\": \"2022-12-18T20:38:20.000+08:00\","+
                "  \"quantity\": 10000," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(9)
    public void postOnSaleTest3() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"price\": 5000," +
                "\"beginTime\": \"2021-12-18T20:38:20.000+08:00\"," +
                "\"endTime\": \"2022-12-18T20:38:20.000+08:00\","+
                "  \"quantity\": 10000," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 开始时间早于结束时间
     * @throws Exception
     */
    @Test
    @Order(9)
    public void postOnSaleTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 500," +
                "\"beginTime\": \"2022-12-12T20:38:20.000+08:00\"," +
                "\"endTime\": \"2021-12-19T20:38:20.000+08:00\","+
                "  \"quantity\": 100," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.LATE_BEGINTIME.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postOnSaleTest5() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 5000," +
                "\"beginTime\": \"2021-12-18T20:38:20.000+08:00\"," +
                "\"endTime\": \"2022-12-18T20:38:20.000+08:00\","+
                "  \"quantity\": 10000," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(0)
                .returnResult().getResponseBody()), "UTF-8");
        SimpleOnSaleRetVo vo = JacksonUtil.parseObject(ret, "data", SimpleOnSaleRetVo.class);
        this.onsaleId1 = vo.getId();
    }
    /**
     * 同时间
     * @throws Exception
     */
    @Test
    @Order(11)
    public void postOnSaleTest6() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 500," +
                "\"beginTime\": \"2021-12-19T20:38:20.000+08:00\"," +
                "\"endTime\": \"2022-12-12T20:38:20.000+08:00\","+
                "  \"quantity\": 100," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.GOODS_PRICE_CONFLICT.getCode());
    }
    /**
     * 时间不冲突
     * @throws Exception
     */
    @Test
    @Order(11)
    public void postOnSaleTest7() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"price\": 500," +
                "\"beginTime\": \"2023-12-19T20:38:20.000+08:00\"," +
                "\"endTime\": \"2024-12-12T20:38:20.000+08:00\","+
                "  \"quantity\": 10002," +
                "  \"type\": 0," +
                "  \"numKey\": 1," +
                "  \"maxQuantity\":2}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(ONSALE, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(0)
                .returnResult().getResponseBody()), "UTF-8");
        SimpleOnSaleRetVo vo = JacksonUtil.parseObject(ret, "data", SimpleOnSaleRetVo.class);
        this.onsaleId2 = vo.getId();
    }
    /**
     * 未上架的商品，有未上线销售
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getProductDetail4() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(1000);;
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getOnSaleTest3() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getOnSaleTest4() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.price").isEqualTo(5000);
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(12)
    public void getOnSaleTest5() throws Exception{
        assertNotNull(this.onsaleId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId2)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(0)
                .jsonPath("$.data.price").isEqualTo(500);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void putProductTest1() throws Exception{
        String json = "{\"weight\": 500}";
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(SHOPPRODUCTID, 1, 598633585)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(12)
    public void putProductTest2() throws Exception{
        assertNotNull(this.productId);
        String json = "{\"weight\": 500}";
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(SHOPPRODUCTID, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(12)
    public void putProductTest3() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("2721900002", "123456");
        String json = "{\"weight\": 500}";
        this.gatewayClient.put().uri(SHOPPRODUCTID, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .header("authorization", token)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(13)
    public void putProductTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"weight\": 500}";
        this.gatewayClient.put().uri(SHOPPRODUCTID, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(1000);

        String ret = new String(Objects.requireNonNull(this.gatewayClient.get().uri(DRAFT + "?page=1&pageSize=50", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试商品1')].weight").isEqualTo(500)
                .jsonPath("$.data.list[?(@.name == '测试商品1')].skuSn").isEqualTo("2222222")
                .jsonPath("$.data.list[?(@.name == '测试商品1')].productId").isEqualTo(this.productId.intValue())
                .returnResult().getResponseBody()),"UTF-8");

        List<ProductNewReturnVo> vos = JacksonUtil.parseSubnodeToObjectList(ret, "/data/list", ProductNewReturnVo.class);
        ProductNewReturnVo vo = vos.stream().filter(o->o.getProductId().equals(this.productId)).findAny().orElse(null);
        assertNotNull(vo);
        this.draftProdId3 = vo.getId();
    }
    /**
     * 平台管理员发布
     * @throws Exception
     */
    @Test
    @Order(14)
    public void publishProductTest7() throws Exception{
        assertNotNull(this.draftProdId3);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(PUBLISH, 0, this.draftProdId3)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue());

        this.gatewayClient.get().uri(DRAFTID, 1, this.draftProdId3)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 未上架的商品，有上线销售
     * @throws Exception
     */
    @Test
    @Order(15)
    public void getProductDetail5() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * 未登录
     * @throws Exception
     */
    @Test
    @Order(15)
    public void onlineOnSaleTest2() throws Exception{
        assertNotNull(this.onsaleId1);
        this.gatewayClient.put().uri(ONSALEONLINE, 1, this.onsaleId1)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NEED_LOGIN.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(15)
    public void onlineOnSaleTest3() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONSALEONLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(16)
    public void onlineOnSaleTest4() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSALEONLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(1);
    }
    /**
     * 未上架的商品，有上线销售
     * @throws Exception
     */
    @Test
    @Order(17)
    public void getProductDetail6() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void onshelfProductTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, 325698)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(17)
    public void onshelfProductTest2() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, this.productId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(17)
    public void onshelfProductTest3() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员上架
     * @throws Exception
     */
    @Test
    @Order(18)
    public void onshelfProductTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPPRODUCTID,1, this.productId)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.state").isEqualTo(2);
    }
    /**
     * 上架的商品，有上线销售
     * @throws Exception
     */
    @Test
    @Order(19)
    public void getProductDetail7() throws Exception {
        assertNotNull(this.productId);
        assertNotNull(this.onsaleId1);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(this.onsaleId1.intValue())
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(5000)
                .jsonPath("$.data.quantity").isEqualTo(10000)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offshelfProductTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFSHELF, 1, 325698)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(19)
    public void offshelfProductTest2() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(OFFSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(20)
    public void offshelfProductTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(OFFSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPPRODUCTID,1, this.productId)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.state").isEqualTo(1);
    }
    /**
     * 未上架的商品，有上线销售
     * @throws Exception
     */
    @Test
    @Order(21)
    public void getProductDetail8() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * 同店铺管理员再次上架
     *
     * @throws Exception
     */
    @Test
    @Order(22)
    public void onshelfProductTest5() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPPRODUCTID,1, this.productId)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.state").isEqualTo(2);
    }
    /**
     * 上架的商品，有上线销售
     * @throws Exception
     */
    @Test
    @Order(23)
    public void getProductDetail9() throws Exception {
        assertNotNull(this.productId);
        assertNotNull(this.onsaleId1);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(this.onsaleId1.intValue())
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(5000)
                .jsonPath("$.data.quantity").isEqualTo(10000)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void offlineOnSaleTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSALEOFFLINE, 1, 1223345)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(24)
    public void offlineOnSaleTest2() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.put().uri(ONSALEOFFLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(24)
    public void offlineOnSaleTest3() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.put().uri(ONSALEOFFLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(25)
    public void offlineOnSaleTest4() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSALEOFFLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody();
        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(2);
    }
    /**
     * 上架的商品，下线销售
     * @throws Exception
     */
    @Test
    @Order(26)
    public void getProductDetail10() throws Exception {
        assertNotNull(this.productId);
        this.mallClient.get().uri(PRODUCTID, this.productId)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.onsaleId").isEqualTo(null)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.price").isEqualTo(null)
                .jsonPath("$.data.quantity").isEqualTo(null)
                .jsonPath("$.data.weight").isEqualTo(500);
    }
    /**
     * 同店铺管理员试图重新上线
     * @throws Exception
     */
    @Test
    @Order(27)
    public void onlineOnSaleTest5() throws Exception{
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSALEONLINE, 1, this.onsaleId1)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());

        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(2);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void delOnSaleTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(ONSALEID, 1, 324235)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 无权限
     * @throws Exception
     */
    @Test
    @Order(27)
    public void delOnSaleTest2() throws Exception{
        assertNotNull(this.onsaleId2);
        String token = this.adminLogin("shop1_coupon", "123456");
        this.gatewayClient.delete().uri(ONSALEID, 1, this.onsaleId2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(cn.edu.xmu.privilegegateway.annotation.util.ReturnNo.AUTH_NO_RIGHT.getCode());
    }
    /**
     * 不同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(27)
    public void delOnSaleTest3() throws Exception{
        assertNotNull(this.onsaleId2);
        String token = this.adminLogin("2721900002", "123456");
        this.gatewayClient.delete().uri(ONSALEID, 1, this.onsaleId2)
                .header("authorization", token)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 同店铺管理员试图删除下线态
     * @throws Exception
     */
    @Test
    @Order(27)
    public void delOnSaleTest4() throws Exception {
        assertNotNull(this.onsaleId1);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(ONSALEID, 1, this.onsaleId1)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }
    /**
     * 同店铺管理员
     * @throws Exception
     */
    @Test
    @Order(28)
    public void delOnSaleTest5() throws Exception{
        assertNotNull(this.onsaleId2);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.delete().uri(ONSALEID, 1, this.onsaleId2)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        this.gatewayClient.get().uri(ONSALEID, 1, this.onsaleId2)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void prohibitProductTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(PROHIBIT, 0, 325698)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 同店铺管理员禁售
     * @throws Exception
     */
    @Test
    @Order(28)
    public void prohibitProductTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(PROHIBIT, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员禁售
     * @throws Exception
     */
    @Test
    @Order(29)
    public void prohibitProductTest5() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(PROHIBIT, 0, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPPRODUCTID,1, this.productId)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.state").isEqualTo(3);
    }

    /**
     * 同店铺管理员上架
     * @throws Exception
     */
    @Test
    @Order(30)
    public void onshelfProductTest7() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ONSHELF, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.STATENOTALLOW.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void allowProductTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(ALLOW, 0, 325698)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 同店铺管理员解禁
     * @throws Exception
     */
    @Test
    @Order(30)
    public void allowProductTest4() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(ALLOW, 1, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /**
     * 平台管理员解禁
     * @throws Exception
     */
    @Test
    @Order(31)
    public void allowProductTest5() throws Exception{
        assertNotNull(this.productId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(ALLOW, 0, this.productId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(SHOPPRODUCTID,1, this.productId)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.productId.intValue())
                .jsonPath("$.data.shop.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试商品1")
                .jsonPath("$.data.skuSn").isEqualTo("2222222")
                .jsonPath("$.data.originalPrice").isEqualTo(100000)
                .jsonPath("$.data.state").isEqualTo(1);
    }
}
