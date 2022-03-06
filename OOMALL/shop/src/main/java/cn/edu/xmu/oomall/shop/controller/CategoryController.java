package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.model.bo.Category;
import cn.edu.xmu.oomall.shop.model.vo.CategoryVo;
import cn.edu.xmu.oomall.shop.service.CategoryService;

import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * 商品分类Controller
 *
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/28
 */
@Api(value = "商品类别API", tags = "商品类别API")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CategoryController {
    @Autowired
    HttpServletResponse httpServletResponse;

    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "查询商品分类关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "种类id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/categories/{id}/subcategories")
    public Object selectCategories(@PathVariable Long id) {
        if (id < 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }

        return selectSubCategories(id);
    }

    @ApiOperation(value = "查询商品上级分类关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "种类id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
    })
    @GetMapping("/categories/{id}/parents")
    public Object selectParentCategory(@PathVariable("id") Long id) {
        ReturnObject ret = categoryService.getParentCategoryById(id);
        return Common.decorateReturnObject(ret);
    }


    @ApiOperation(value = "查询没有一级分类的二级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "种类id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/orphancategories")
    public Object selectOrphoncategories(@PathVariable("shopId") Long shopId) {
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return selectSubCategories(-1L);
    }


    @ApiOperation(value = "管理员新增商品类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "父类别id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 901, message = "类目名称已存在"),
            @ApiResponse(code = 967, message = "不允许增加新的下级分类")
    })
    @Audit(departName = "shops")
    @PostMapping("/shops/{shopId}/categories/{id}/subcategories")
    public Object addCategories(@PathVariable("id") Long id,
                                @PathVariable("shopId") Long shopId,
                                @LoginUser Long createId,
                                @LoginName String createName,
                                @Valid @RequestBody CategoryVo vo,
                                BindingResult bindingResult) {
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // 非法输入
        if (id < 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }
        // vo合法性检查
        var res = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        Category cate = cloneVo(vo, Category.class);
        ReturnObject ret = categoryService.newCategory(id, cate, createId, createName);

        if (ret.getCode() == ReturnNo.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }

        return Common.decorateReturnObject(ret);
    }


    @ApiOperation(value = "管理员修改商品类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization ", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "类别id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 901, message = "类目名称已存在")
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/categories/{id}")
    public Object changeCategories(@PathVariable("id") Long id,
                                   @PathVariable("shopId") Long shopId,
                                   @LoginUser Long modifyId,
                                   @LoginName String modiName,
                                   @Valid @RequestBody CategoryVo vo,
                                   BindingResult bindingResult) {
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // vo合法性检查
        var res = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }

        Category cate = cloneVo(vo, Category.class);
        ReturnObject ret = categoryService.changeCategory(id, cate, modifyId, modiName);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "管理员删除商品类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "种类id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
    })
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/categories/{id}")
    public Object deleteCategories(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // 若id为0或-1时不允许删除
        if (id <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }

        ReturnObject ret = categoryService.deleteCategoryById(id);
        return Common.decorateReturnObject(ret);
    }

    private Object selectSubCategories(Long id) {
        ReturnObject ret = categoryService.getSubCategories(id);

        return Common.decorateReturnObject(ret);
    }

    /**
     * @author 何赟
     * @date 2021-12-5
     */
    @GetMapping("/internal/categories/{id}")
    public Object getCategoryById(@PathVariable("id")Long id){
        ReturnObject ret = categoryService.getCategoryById(id);
        if(ret.getCode()!=ReturnNo.OK){
            return new InternalReturnObject(ret.getCode().getCode(),"分类不存在");
        }
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/internal/categories/{id}/detail")
    public Object getCategoryDetail(@PathVariable("id")Long id){
        return categoryService.getCategoryById(id);
    }
}
