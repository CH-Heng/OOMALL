package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.*;
import org.checkerframework.checker.guieffect.qual.PolyUIType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;


/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Api(value = "商品", tags = "goods")
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ProductService productService;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value="查看运费模板用到的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="fid",value="运费模板id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")

    })
    @GetMapping("shops/{shopId}/freightmodels/{fid}/products")
    @Audit(departName = "shops")
    public Object getFreightProducts(@PathVariable("shopId") Long shopId, @PathVariable("fid") Long fid, @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize, @LoginUser Long loginUserId, @LoginName String loginUserName) {
        ReturnObject retVoObject =
                productService.listProductsByFreightId(shopId,fid, page, pageSize);
        return Common.decorateReturnObject(retVoObject);
    }

    @ApiOperation(value="新建商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="goodsVo",value="商品集合详细信息",dataType = "GoodsVo",paramType = "body"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })

    @PostMapping("shops/{id}/goods")
    @Audit(departName = "shops")
    public Object insertGoods(@PathVariable("id") Long shopId, @Validated @RequestBody GoodsVo goodsVo,
                              BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }
        ReturnObject ro = goodsService.insertGoods(shopId, goodsVo,loginUserId,loginUserName);
        return Common.decorateReturnObject(ro);
    }

    @ApiOperation(value="修改特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="goodsVo",value="商品集合详细信息",dataType = "GoodsVo",paramType = "body"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code=503,message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping("shops/{shopId}/goods/{id}")
    @Audit(departName = "shops")
    public Object updateGoods(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @Valid @RequestBody GoodsVo goodsVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }
        return Common.decorateReturnObject(goodsService.updateGoods(shopId,id, goodsVo,loginUserId,loginUserName));

    }

    @ApiOperation(value="获取特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping(value="/shops/{shopId}/goods/{id}" )
    @Audit(departName = "shops")
    public Object searchGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(goodsService.searchById(shopId, id));
    }

    @ApiOperation(value="删除特定商品集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="商品集合id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @DeleteMapping(value="shops/{shopId}/goods/{id}")
    @Audit(departName = "shops")
    public Object deleteGoods(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(goodsService.deleteGoods(shopId,id));
    }


    @ApiOperation(value="发布货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")
    })
    @PutMapping(value="shops/{shopId}/draftproducts/{id}/publish")
    @Audit(departName = "shops")
    public Object publishProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.publishProduct(shopId,id));
    }

    @ApiOperation(value="上架货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/onshelves")
    @Audit(departName = "shops")
    public Object onshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.onshelvesProduct(shopId,id));
    }

    @ApiOperation(value="下架货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/offshelves")
    @Audit(departName = "shops")
    public Object offshelvesProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.offshelvesProduct(shopId,id));
    }

    @ApiOperation(value="解禁货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/allow")
    @Audit(departName = "shops")
    public Object allowProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.allowProduct(shopId,id));
    }

    @ApiOperation(value="禁售货品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name="id",value="货品id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="loginUser",value="用户登录账号(id)",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name="loginUser",value="用户登录名",required = true,dataType = "String",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code=505,message = "操作的资源id不是自己的对象")
    })
    @PutMapping(value="shops/{shopId}/products/{id}/prohibit")
    @Audit(departName = "shops")
    public Object prohibitProduct(@PathVariable("shopId")Long shopId,@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginUserName)
    {
        return Common.decorateReturnObject(productService.prohibitProduct(shopId,id));
    }

    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @ApiOperation(value="获得二级分类下的上架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="分类id",required = true,dataType = "integer",paramType = "path"),
            @ApiImplicitParam(name="page",value="页码",required = true,dataType = "integer",paramType = "query"),
            @ApiImplicitParam(name="pageSize",value="每页数目",required = true,dataType = "integer",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "分类id不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping(value="categories/{id}/products")
    public Object getProductOfCategory(@PathVariable("id") Long id,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject ret = productService.getProductsOfCategories(null, id,page,pageSize);
        if (ret.getCode().getCode() !=0){
            return Common.decorateReturnObject(ret);
        }
        return  Common.decorateReturnObject(Common.getPageRetVo(ret, SimpleProductRetVo.class));
    }

    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @ApiOperation(value="获得二级分类下的店铺的所有商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name="did",value="商铺id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="id",value="分类id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name="page",value="页码",required = true,dataType = "integer",paramType = "query"),
            @ApiImplicitParam(name="pageSize",value="每页数目",required = true,dataType = "integer",paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "分类id不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping(value="shops/{did}/categories/{id}/products" )
    @Audit(departName = "shops")
    public Object getProductOfCategoryInShop(@PathVariable("did") Long did,@PathVariable("id") Long cid,
                                             @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize){
        ReturnObject ret = productService.getProductsOfCategories(did,cid,page,pageSize);
        if (ret.getCode().getCode() !=0){
            return Common.decorateReturnObject(ret);
        }
        return  Common.decorateReturnObject(Common.getPageRetVo(ret, SimpleProductRetVo.class));
    }


    /**
     * 获得货品的所有状态
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "获得货品的所有状态")
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping(value = "/products/states")
    public Object getProductStates() {
        ReturnObject<List> returnObject = productService.getProductStates();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询商品
     *
     * @param shopId,barCode,page,pageSize
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "查询商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "barCode", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", dataType = "Integer", paramType = "query")
    })
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping(value = "/products")
    public Object getProducts(@RequestParam(value = "shopId",required = false) Long shopId,
                              @RequestParam(value = "barCode",required = false) String barCode,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize) {

        ReturnObject returnObject = productService.getAllProducts(shopId,barCode, page, pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 获得product的详细信息
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "获得product的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "String", paramType = "path")
    })
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping("products/{id}")
    public Object getProductDetails(@PathVariable("id") Long id) {
        ReturnObject ret = productService.getProductDetails(id);
        return Common.decorateReturnObject(ret);
    }


    /**
     * 管理员添加新的Product到Goods里
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "管理员添加新的Product到Goods里")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903,message ="不允许加入一级分类")
    })
    @PostMapping("/shops/{shopId}/draftproducts")
    @Audit(departName = "shops")
    public Object addProductToGood(@PathVariable Long shopId, @Validated @RequestBody ProductDetailVo productVo,
                                   BindingResult bindingResult,@LoginUser Long userId, @LoginName String userName) {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }

        ReturnObject ret = productService.addProductToGood(shopId, productVo, userId, userName);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 上传货品图片
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "上传货品图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作"),
    })
    @PostMapping("/shops/{shopId}/draftproducts/{id}/uploadImg")
    @Audit(departName = "shops")
    public Object uploadProductImg(@RequestHeader("authorization") String authorization,
                                   @PathVariable Long shopId, @PathVariable Long id,
                                   MultipartFile multipartFile, @LoginUser Long userId, @LoginName String userName) {
        ReturnObject returnObject = productService.upLoadProductImg(shopId, id, multipartFile, userId, userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员或店家物理删除审核态的Products
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "管理员或店家删除Products")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @DeleteMapping("/shops/{shopId}/draftproducts/{id}")
    @Audit(departName = "shops")
    public Object deleteProduct(@PathVariable Long id, @PathVariable Long shopId,@LoginUser Long userId, @LoginName String userName) {
        ReturnObject returnObject = productService.deleteDraftProductById(shopId, id,userId,userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家修改审核态货品信息
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/12
     */
    @ApiOperation(value = "店家修改审核态货品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("/shops/{shopId}/draftproducts/{id}")
    @Audit(departName = "shops")
    public Object changeDraftProduct(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,
                                     @Validated @RequestBody ProductChangeVo productChangeVo,
                                     BindingResult bindingResult,@LoginUser Long userId, @LoginName String userName) {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }

        ReturnObject returnObject = productService.updateDraftProduct(shopId, id, productChangeVo, userId,userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家查看货品详细信息
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/11
     */
    @ApiOperation(value = "店家查看货品详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping("/shops/{shopId}/products/{id}")
    @Audit(departName = "shops")
    public Object getShopProductDetails(@PathVariable Long shopId, @PathVariable Long id,@LoginUser Long userId, @LoginName String userName) {
        ReturnObject returnObject = productService.getShopProductDetails(shopId,id,userId,userName);

        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员查看本店铺未审核商品
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/11
     */
    @ApiOperation(value = "管理员查看本店铺未审核商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "page", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", dataType = "Integer", paramType = "query")
    })
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping("/shops/{shopId}/draftproducts")
    @Audit(departName = "shops")
    public Object getShopDraftProduct(@PathVariable Long shopId, @LoginUser Long userId, @LoginName String userName,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer pageSize) {
        ReturnObject returnObject = productService.getShopDraftProduct(shopId, page, pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家查看审核态货品详细信息
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/11
     */
    @ApiOperation(value = "店家查看审核态货品详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({@ApiResponse(code = 0, message = "成功"),})
    @GetMapping("/shops/{shopId}/draftproducts/{id}")
    @Audit(departName = "shops")
    public Object getShopDraftProductDetails(@PathVariable Long shopId, @PathVariable Long id,
                                             @LoginUser Long userId, @LoginName String userName) {
        ReturnObject returnObject = productService.getShopDraftProductDetails(shopId, id, userId, userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家修改下线态货品信息
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/12
     */
    @ApiOperation(value = "店家修改下线态货品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "skuId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("/shops/{shopId}/products/{id}")
    @Audit(departName = "shops")
    public Object changeProduct(@PathVariable Long shopId, @PathVariable Long id,
                                @Validated @RequestBody ProductChangeVo productChangeVo,
                                BindingResult bindingResult,@LoginUser Long userId, @LoginName String userName) {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"传入的RequestBody参数格式不合法"));
        }

        ReturnObject returnObject = productService.changeProduct(shopId, id, productChangeVo, userId, userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查看商品集合中的商品（无需登录）
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @ApiOperation(value = "查看商品集合中的商品（无需登录）")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "id", value = "商品id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/goods/{id}")
    public Object getGoodsProduct(@PathVariable("id") Long id) {
        ReturnObject ret = productService.getGoodsProductById(id);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 内部API-将上线态的秒杀商品加载到Redis
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/16
     */
    @ApiOperation(value = "将上线态的秒杀商品加载到Redis")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "endTime", dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 510, message = "缺少必要参数"),
    })
    @GetMapping("/internal/secondkillproducts/load")
    public Object loadSecondKillProduct(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false) ZonedDateTime beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false) ZonedDateTime endTime) {
        if(beginTime == null || endTime == null){
            return new ReturnObject<>(ReturnNo.PARAMETER_MISSED);
        }
        LocalDateTime begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return Common.decorateReturnObject(productService.loadSecondKillProduct(begin, end));
    }

    /**
     * 获取商品运费模板
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/12/4
     */
    @ApiOperation(value = "获取商品运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "skuId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/products/{id}/freightmodels")
    public Object getFreightModels(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,
                                   @LoginUser Long userId, @LoginName String userName) {
        ReturnObject returnObject = productService.getFreightModels(shopId,id,userId,userName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员修改商铺的运费定义
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/12/4
     */
    @ApiOperation(value = "管理员修改商铺的运费定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "productId", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "fid", value = "freightModelsId", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit(departName = "shops")
    @PostMapping("/shops/{shopId}/products/{id}/freightmodels/{fid}")
    public Object changeFreightModels(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id,@PathVariable("fid") Long fid,
                                      @LoginUser Long userId, @LoginName String userName) {
        if(shopId!=0){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject returnObject = productService.changeFreightModels(id,fid,userId,userName);
        if(returnObject.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return Common.decorateReturnObject(returnObject);
    }

    //----------------内部API--------------------//

    @GetMapping("/products/{id}/exist")
    public Object existProduct(@PathVariable("id")Long id){
        return Common.decorateReturnObject(productService.existProduct(id));
    }

    /**
     * 获得product的详细信息(包含分类佣金)
     *
     * @param
     * @return Object
     * @author wyg
     * @Date 2021/11/10
     */
    @GetMapping("/internal/products/{id}")
    public Object getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id couponActId
     * @return 是否存在
     */
    @GetMapping("/internal/product/{id}/exist")
    public Object isProductExist(@PathVariable Long id){
        ReturnObject ret = productService.isProductExit(id);
        return Common.decorateReturnObject(ret);
    }

}
