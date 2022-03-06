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
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FreightTest extends BaseTestOomall {

    private static final String MODEL = "/freight/shops/{shopId}/freightmodels";
    private static final String DEFAULT = "/freight/shops/{shopId}/freightmodels/default";
    private static final String CLONE = "/freight/shops/{shopId}/freightmodels/{id}/clone";
    private static final String MODELID = "/freight/shops/{shopId}/freightmodels/{id}";
    private static final String WEIGHTMODEL = "/freight/shops/{shopId}/freightmodels/{id}/weightitems";
    private static final String PIECEMODEL = "/freight/shops/{shopId}/freightmodels/{id}/pieceitems";
    private static final String PRICE = "/freight/regions/{rid}/price";
    private static final String PRODFEIRGHT = "/goods/shops/{shopId}/products/{id}/freightmodels";
    private static final String POSTFEIRGHT = "/goods/shops/{shopId}/products/{id}/freightmodels/{fid}";
    private static final String FREIGHTPROD = "/goods/shops/{shopId}/freightmodels/{fid}/products";

    private static Long weightModelId = null;
    private static Long pieceModelId = null;
    private static Long cloneModelId = null;
    private static Long weightItem_xiamen = null;
    private static Long pieceItem_xiamen = null;
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    public void getDefaultModelTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(DEFAULT, 0)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("freight model/100g")
                .jsonPath("$.data.unit").isEqualTo(100);
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    public void postModelTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        String json = "{\"name\": \"测试模板1\"," +
                "\"type\": 0," +
                "  \"unit\": 50," +
                "  \"defaultModel\": 0}";
        this.gatewayClient.post().uri(MODEL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postModelTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json = "{\"name\": \"测试重量模板1\"," +
                "\"type\": 0," +
                "  \"unit\": 50," +
                "  \"defaultModel\": 0}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(MODEL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试重量模板1")
                .jsonPath("$.data.unit").isEqualTo(50)
                .returnResult().getResponseBody()), "UTF-8");

        this.weightModelId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);
        this.gatewayClient.get().uri(MODELID, 0, this.weightModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.weightModelId.intValue())
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.name").isEqualTo("测试重量模板1");

    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postModelTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        String json = "{\"name\": \"测试件数模板1\"," +
                "\"type\": 1," +
                "  \"unit\": 0," +
                "  \"defaultModel\": 0}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(MODEL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.name").isEqualTo("测试件数模板1")
                .jsonPath("$.data.unit").isEqualTo(0)
                .returnResult().getResponseBody()), "UTF-8");

        this.pieceModelId = JacksonUtil.parseSubnodeToObject(ret, "/data/id",Long.class);

        this.gatewayClient.get().uri(MODELID, 0, this.pieceModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.pieceModelId.intValue())
                .jsonPath("$.data.type").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试件数模板1")
                .jsonPath("$.data.unit").isEqualTo(0);
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    public void getModelTest1() throws Exception{
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(MODEL, 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getModelTest2() throws Exception {
        assertNotNull(this.pieceModelId);
        assertNotNull(this.weightModelId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(MODEL, 0)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.name == '测试重量模板1')].id").isEqualTo(this.weightModelId.intValue())
                .jsonPath("$.data.list[?(@.name == '测试件数模板1')].id").isEqualTo(this.pieceModelId.intValue());
    }
    /**
     * id不存在，返回默认模板
     * @throws Exception
     */
    @Test
    public void getModelIDTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(MODELID, 0, 111122)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.name").isEqualTo("freight model/100g")
                .jsonPath("$.data.unit").isEqualTo(100);
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getModelIDTest2() throws Exception{
        assertNotNull(this.weightModelId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.get().uri(MODELID, 0, this.weightModelId)
                .header("authorization", token)
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
    public void putModelIDTest1() throws Exception{
        String json = "{\"name\": \"测试件数模板11\"}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(MODELID, 0, 111122)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(2)
    public void putModelIDTest2() throws Exception{
        assertNotNull(this.pieceModelId);
        String json = "{\"name\": \"测试件数模板11\"}";
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.put().uri(MODELID, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void putModelIDTest3() throws Exception{
        assertNotNull(this.pieceModelId);
        String json = "{\"name\": \"测试件数模板11\"}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(MODELID, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        this.gatewayClient.get().uri(MODELID, 0, this.pieceModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.pieceModelId.intValue())
                .jsonPath("$.data.type").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试件数模板11")
                .jsonPath("$.data.unit").isEqualTo(0);
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postPieceTest1() throws Exception{
        String json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 1000," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 1000," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(PIECEMODEL, 0, 111122)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postPieceTest2() throws Exception{
        assertNotNull(this.pieceModelId);
        String json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 1000," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 1000," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * 福建省
     * 厦门市
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postPieceTest3() throws Exception{
        assertNotNull(this.pieceModelId);
        String json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 1000," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 1000," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '14')].firstItems").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '14')].firstItemFreight").isEqualTo(1000)
                .jsonPath("$.data.list[?(@.regionId == '14')].additionalItems").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '14')].additionalItemsPrice").isEqualTo(1000);

        //厦门市
        json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 500," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 500," +
                "  \"regionId\": 151}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.pieceItem_xiamen = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);
        this.gatewayClient.get().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.regionId == '151')].firstItems").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '151')].firstItemFreight").isEqualTo(500)
                .jsonPath("$.data.list[?(@.regionId == '151')].additionalItems").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '151')].additionalItemsPrice").isEqualTo(500);
    }
    /**
     * 平台管理员
     * 重复厦门
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postPieceTest4() throws Exception{
        assertNotNull(this.pieceModelId);
        String token = this.adminLogin("13088admin", "123456");
        //厦门市
        String json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 500," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 500," +
                "  \"regionId\": 151}";
        this.gatewayClient.post().uri(PIECEMODEL, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FREIGHT_REGIONEXIST.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postWeightTest1() throws Exception{
        String json = "{\"firstWeight\":100," +
                "\"firstWeightFreight\": 1000," +
                "  \"tenPrice\": 100," +
                "  \"fiftyPrice\": 100," +
                "  \"hundredPrice\": 100," +
                "  \"trihunPrice\": 100," +
                "  \"abovePrice\": 100," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(WEIGHTMODEL, 0, 111122)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(5)
    public void postWeightTest2() throws Exception{
        assertNotNull(this.weightModelId);
        String json = "{\"firstWeight\":100," +
                "\"firstWeightFreight\": 1000," +
                "  \"tenPrice\": 100," +
                "  \"fiftyPrice\": 100," +
                "  \"hundredPrice\": 100," +
                "  \"trihunPrice\": 100," +
                "  \"abovePrice\": 100," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * 福建省
     * @throws Exception
     */
    @Test
    @Order(6)
    public void postWeightTest5() throws Exception{
        assertNotNull(this.weightModelId);
        String json = "{\"firstWeight\":100," +
                "\"firstWeightFreight\": 1000," +
                "  \"tenPrice\": 100," +
                "  \"fiftyPrice\": 100," +
                "  \"hundredPrice\": 100," +
                "  \"trihunPrice\": 100," +
                "  \"abovePrice\": 100," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.regionId == '14')].firstWeight").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '14')].firstWeightFreight").isEqualTo(1000)
                .jsonPath("$.data.list[?(@.regionId == '14')].tenPrice").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '14')].fiftyPrice").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '14')].hundredPrice").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '14')].trihunPrice").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '14')].abovePrice").isEqualTo(100);
        //厦门市
        json = "{\"firstWeight\":100," +
                "\"firstWeightFreight\": 500," +
                "  \"tenPrice\": 50," +
                "  \"fiftyPrice\": 50," +
                "  \"hundredPrice\": 50," +
                "  \"trihunPrice\": 50," +
                "  \"abovePrice\": 50," +
                "  \"regionId\": 151}";
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()), "UTF-8");

        this.weightItem_xiamen = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);

        this.gatewayClient.get().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(2)
                .jsonPath("$.data.list[?(@.regionId == '151')].firstWeight").isEqualTo(100)
                .jsonPath("$.data.list[?(@.regionId == '151')].firstWeightFreight").isEqualTo(500)
                .jsonPath("$.data.list[?(@.regionId == '151')].tenPrice").isEqualTo(50)
                .jsonPath("$.data.list[?(@.regionId == '151')].fiftyPrice").isEqualTo(50)
                .jsonPath("$.data.list[?(@.regionId == '151')].hundredPrice").isEqualTo(50)
                .jsonPath("$.data.list[?(@.regionId == '151')].trihunPrice").isEqualTo(50)
                .jsonPath("$.data.list[?(@.regionId == '151')].abovePrice").isEqualTo(50);
    }
    /**
     * 平台管理员
     * 重复福建
     * @throws Exception
     */
    @Test
    @Order(7)
    public void postWeightTest6() throws Exception {
        assertNotNull(this.weightModelId);
        String json = "{\"firstWeight\":100," +
                "\"firstWeightFreight\": 1000," +
                "  \"tenPrice\": 100," +
                "  \"fiftyPrice\": 100," +
                "  \"hundredPrice\": 100," +
                "  \"trihunPrice\": 100," +
                "  \"abovePrice\": 100," +
                "  \"regionId\": 14}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(WEIGHTMODEL, 0, this.weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FREIGHT_REGIONEXIST.getCode());
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void postProdFreightTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(POSTFEIRGHT, 0, 4906, 111122)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(7)
    public void postProdFreightTest2() throws Exception{
        assertNotNull(this.weightModelId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(POSTFEIRGHT, 0, 4906, weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(8)
    public void postProdFreightTest3() throws Exception{
        assertNotNull(this.weightModelId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(POSTFEIRGHT, 0, 4906, weightModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PRODFEIRGHT, 0, 4906)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.weightModelId.intValue());

        this.gatewayClient.get().uri(FREIGHTPROD, 0, this.weightModelId.intValue())
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4906')]").exists();
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(8)
    public void postProdFreightTest4() throws Exception{
        assertNotNull(this.pieceModelId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(POSTFEIRGHT, 0, 4929, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PRODFEIRGHT, 0, 4929)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.pieceModelId.intValue());

        this.gatewayClient.get().uri(FREIGHTPROD, 0, this.pieceModelId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4929')]").exists();
    }
    /**
     * id不存在
     * @throws Exception
     */
    @Test
    public void getProdFreight1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(PRODFEIRGHT, 0, 111122)
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
    public void getFreightProd1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(FREIGHTPROD, 0, 111122)
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
    public void cloneFreightTest1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(CLONE, 0, 111122)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }
    /**
     * 店铺1管理员
     * @throws Exception
     */
    @Test
    @Order(8)
    public void cloneFreightTest2() throws Exception{
        assertNotNull(this.pieceModelId);
        String token = this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(CLONE, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(9)
    public void cloneFreightTest3() throws Exception{
        assertNotNull(this.pieceModelId);
        String token = this.adminLogin("13088admin", "123456");
        String ret = new String(Objects.requireNonNull(this.gatewayClient.post().uri(CLONE, 0, this.pieceModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .returnResult().getResponseBody()),"UTF-8");
        this.cloneModelId = JacksonUtil.parseSubnodeToObject(ret, "/data/id", Long.class);
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(10)
    public void putModelIDTest4() throws Exception{
        assertNotNull(this.cloneModelId);
        String json = "{\"name\": \"测试件数模板2\"}";
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.put().uri(MODELID, 0, this.cloneModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());
        this.gatewayClient.get().uri(MODELID, 0, this.cloneModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.cloneModelId.intValue())
                .jsonPath("$.data.type").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("测试件数模板2")
                .jsonPath("$.data.unit").isEqualTo(0);
    }
    /**
     * 平台管理员
     * 莆田
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postPieceTest5() throws Exception{
        assertNotNull(this.cloneModelId);
        String token = this.adminLogin("13088admin", "123456");
        //厦门市
        String json = "{\"firstItems\":1," +
                "\"firstItemFreight\": 750," +
                "  \"additionalItems\": 1," +
                "  \"additionalItemsPrice\": 750," +
                "  \"regionId\": 152}";
        this.gatewayClient.post().uri(PIECEMODEL, 0, this.cloneModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PIECEMODEL, 0, this.cloneModelId)
                .header("authorization", token)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(3)
                .jsonPath("$.data.list[?(@.regionId == '14')]").exists()
                .jsonPath("$.data.list[?(@.regionId == '151')]").exists()
                .jsonPath("$.data.list[?(@.regionId == '152')]").exists();
    }
    /**
     * 平台管理员
     * @throws Exception
     */
    @Test
    @Order(11)
    public void postProdFreightTest5() throws Exception{
        assertNotNull(this.cloneModelId);
        String token = this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(POSTFEIRGHT, 0, 4934, this.cloneModelId)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(PRODFEIRGHT, 0, 4934)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(this.cloneModelId.intValue());

        this.gatewayClient.get().uri(FREIGHTPROD, 0, this.cloneModelId)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '4934')]").exists();
    }
    /**
     * 翔安区
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postReginPriceTest1() throws Exception {
        assertNotNull(this.cloneModelId);
        assertNotNull(this.pieceModelId);
        assertNotNull(this.weightModelId);
        String token = this.customerLogin("customer1", "123456");
        String json = "[{\"productId\": 4906, \"quantity\": 1, \"freightId\": "+ this.weightModelId +",\"weight\": 250}," +
                "{\"productId\": 4929,\"quantity\": 2,\"freightId\": "+ this.pieceModelId +", \"weight\": 150}," +
                "{\"productId\": 4934,\"quantity\": 3,\"freightId\": "+ this.cloneModelId +", \"weight\": 150}," +
                "{\"productId\": 4893,\"quantity\": 2,\"freightId\": 1, \"weight\": 20}," +
                "{\"productId\": 4901,\"quantity\": 1,\"freightId\": 1, \"weight\": 4}]";
        this.mallClient.post().uri(PRICE, 1604)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.freightPrice").isEqualTo(4500)
                .jsonPath("$.data.productId").isEqualTo(4929);
        /*
        total weight = 250+2*150+3*150+2*20+1*4 = 1044, total pieces = 9
        weightModelId: 500+944/50*50=1444
        1: 1000+544/100*100 = 1544
        pieceModelId: 500+8*500 = 4500
        cloneModelId: 500+8*500 = 4500
         */
    }
    /**
     * 仙游
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postReginPriceTest2() throws Exception {
        assertNotNull(this.cloneModelId);
        assertNotNull(this.pieceModelId);
        assertNotNull(this.weightModelId);
        String token = this.customerLogin("customer1", "123456");
        String json = "[{\"productId\": 4906, \"quantity\": 1, \"freightId\": "+ this.weightModelId +",\"weight\": 250}," +
                "{\"productId\": 4929,\"quantity\": 2,\"freightId\": "+ this.pieceModelId +", \"weight\": 150}," +
                "{\"productId\": 4934,\"quantity\": 3,\"freightId\": "+ this.cloneModelId +", \"weight\": 150}," +
                "{\"productId\": 4893,\"quantity\": 2,\"freightId\": 1, \"weight\": 20}," +
                "{\"productId\": 4901,\"quantity\": 1,\"freightId\": 1, \"weight\": 4}]";
        this.mallClient.post().uri(PRICE, 1610)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.freightPrice").isEqualTo(9000)
                .jsonPath("$.data.productId").isEqualTo(4929);
        /*
        total weight = 250+2*150+3*150+2*20+1*4 = 1044, total pieces = 9
        weightModelId: 1000+944/50*100=2888
        1: 1000+544/100*100 = 1544
        pieceModelId: 1000+8*1000 = 9000
        cloneModelId: 750+8*750 = 6750
         */
    }

    /**
     * 长乐
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postReginPriceTest3() throws Exception {
        assertNotNull(this.cloneModelId);
        assertNotNull(this.pieceModelId);
        assertNotNull(this.weightModelId);
        String token = this.customerLogin("customer1", "123456");
        String json = "[{\"productId\": 4906, \"quantity\": 1, \"freightId\": "+ this.weightModelId +",\"weight\": 250}," +
                "{\"productId\": 4929,\"quantity\": 2,\"freightId\": "+ this.pieceModelId +", \"weight\": 150}," +
                "{\"productId\": 4934,\"quantity\": 3,\"freightId\": "+ this.cloneModelId +", \"weight\": 150}," +
                "{\"productId\": 4893,\"quantity\": 2,\"freightId\": 1, \"weight\": 20}," +
                "{\"productId\": 4901,\"quantity\": 1,\"freightId\": 1, \"weight\": 4}]";
        this.mallClient.post().uri(PRICE, 1597)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FREIGHT_REGION_NOTREACH.getCode());
        /*
        total weight = 250+2*150+3*150+2*20+1*4 = 1044, total pieces = 9
        weightModelId: 1000+944/50*100=2888
        1: 不能送达
        pieceModelId: 1000+8*1000 = 9000
        cloneModelId: 1000+8*1000 = 9000
         */
    }

    /**
     * 西湖
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postReginPriceTest4() throws Exception {
        assertNotNull(this.cloneModelId);
        assertNotNull(this.pieceModelId);
        assertNotNull(this.weightModelId);
        String token = this.customerLogin("customer1", "123456");
        String json = "[{\"productId\": 4906, \"quantity\": 1, \"freightId\": "+ this.weightModelId +",\"weight\": 250}," +
                "{\"productId\": 4929,\"quantity\": 2,\"freightId\": "+ this.pieceModelId +", \"weight\": 150}," +
                "{\"productId\": 4934,\"quantity\": 3,\"freightId\": "+ this.cloneModelId +", \"weight\": 150}," +
                "{\"productId\": 4893,\"quantity\": 2,\"freightId\": 1, \"weight\": 20}," +
                "{\"productId\": 4901,\"quantity\": 1,\"freightId\": 1, \"weight\": 4}]";
        this.mallClient.post().uri(PRICE, 1363)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.FREIGHT_REGION_NOTREACH.getCode());
        /*
        total weight = 250+2*150+3*150+2*20+1*4 = 1044, total pieces = 9
        weightModelId: 1000+944/50*100=2888
        1: 1000+544/100*100 = 1544
        pieceModelId: 不能送达
        cloneModelId: 不能送达
         */
    }
}
