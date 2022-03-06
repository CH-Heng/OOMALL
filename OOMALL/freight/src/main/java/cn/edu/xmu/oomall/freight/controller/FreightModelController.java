package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ResponseUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.model.vo.FreightCalculatingPostVo;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelInfoVo;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelRetVo;
import cn.edu.xmu.oomall.freight.service.FreightModelService;
import cn.edu.xmu.oomall.freight.util.ValidList;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Api(value = "运费模板", tags = "运费模板")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class FreightModelController {

    @Autowired
    FreightModelService freightModelService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    @ApiOperation(value = "管理员定义运费模板",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误"),
            @ApiResponse(code=504,message = "操作的资源id不存在")
    })
    @Audit(departName = "shops")
    @PostMapping("shops/{shopId}/freightmodels")
    public Object addFreightModel(@PathVariable Long shopId, @Validated @RequestBody FreightModelInfoVo freightModelInfo,
                                  BindingResult bindingResult, @LoginUser Long userId, @LoginName String userName) {
        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (obj != null) {
            return obj;
        }
        // 非管理员返回错误
        if (shopId != 0) {
            return new ResponseEntity(ResponseUtil.fail(ReturnNo.RESOURCE_ID_OUTSCOPE, "操作的资源id不是自己的对象"), HttpStatus.FORBIDDEN);
        }
        ReturnObject ret=freightModelService.addFreightModel(freightModelInfo, userId, userName);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "获得商品的运费模板",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误"),
            @ApiResponse(code=504,message = "操作的资源id不存在")
    })
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/freightmodels")
    public Object showFreightModel(@PathVariable Long shopId, @RequestParam(required = false) String name,
                                   @RequestParam(required = false,defaultValue = "1") Integer page,
                                   @RequestParam(required = false,defaultValue = "5") Integer pageSize) {
        ReturnObject ret=freightModelService.showFreightModel(name, page, pageSize);
        return Common.decorateReturnObject(ret);
    }



    @ApiOperation(value = "管理员克隆运费模板",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="模板id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误"),
            @ApiResponse(code=504,message = "操作的资源id不存在")
    })
    @Audit(departName = "shops")
    @PostMapping("shops/{shopId}/freightmodels/{id}/clone")
    public Object cloneFreightModel(@PathVariable Long shopId, @PathVariable Long id,
                                    @LoginUser Long userId, @LoginName String userName) {
        if (shopId != 0) {
            return new ResponseEntity(ResponseUtil.fail(ReturnNo.RESOURCE_ID_OUTSCOPE, "操作的资源id不是自己的对象"), HttpStatus.FORBIDDEN);
        }
        ReturnObject ret=freightModelService.cloneFreightModel(id, userId, userName);
        ret = Common.getRetVo(ret, FreightModelRetVo.class);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "获得默认运费模板详情",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="模板id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/freightmodels/default")
    public Object showDefaultFreightModel(@PathVariable Long shopId) {
        ReturnObject ret=freightModelService.getDefaultFreightModel();
        ret = Common.getRetVo(ret, FreightModelRetVo.class);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "获得运费模板详情",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="模板id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误")
    })
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/freightmodels/{id}")
    public Object showFreightModelById(@PathVariable Long shopId, @PathVariable Long id) {
        ReturnObject ret=freightModelService.showFreightModelById(id);
        ret=Common.getRetVo(ret,FreightModelRetVo.class);
        return Common.decorateReturnObject(ret);
    }


    @ApiOperation(value = "管理员修改运费模板",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="模板id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误"),
            @ApiResponse(code=504,message = "操作的资源id不存在")
    })
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/freightmodels/{id}")
    public Object updateFreightModel(@PathVariable Long shopId, @PathVariable Long id,@RequestBody FreightModelInfoVo freightModelInfo,
                                     @LoginUser Long userId, @LoginName String userName) {
        if (freightModelInfo.getDefaultModel() == null) {
            freightModelInfo.setDefaultModel((byte)0);
        }
        if (shopId != 0) {
            return new ResponseEntity(ResponseUtil.fail(ReturnNo.RESOURCE_ID_OUTSCOPE, "操作的资源id不是自己的对象"), HttpStatus.FORBIDDEN);
        }
        return Common.decorateReturnObject(freightModelService.updateFreightModel(id, freightModelInfo, userId, userName));
    }

    @ApiOperation(value = "管理员删除运费模板",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value ="商铺id" ,required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="模板id" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=500,message = "服务器内部错误"),
            @ApiResponse(code=504,message = "操作的资源id不存在"),
            @ApiResponse(code=998,message = "存在上架销售商品，不能删除运费模板")
    })
    @Audit(departName = "shops")
    @DeleteMapping("shops/{shopId}/freightmodels/{id}")
    public Object deleteFreightModel(@PathVariable Long shopId, @PathVariable Long id) {
        if (shopId != 0) {
            return new ResponseEntity(ResponseUtil.fail(ReturnNo.RESOURCE_ID_OUTSCOPE, "操作的资源id不是自己的对象"), HttpStatus.FORBIDDEN);
        }
        return Common.decorateReturnObject(freightModelService.deleteFreightModel(id));
    }


    @ApiOperation(value = "计算一批商品的运费", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "rid", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ValidList", name = "items", value = "订单商品的订货详情", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @PostMapping("/regions/{rid}/price")
    public Object calculateFreight(@PathVariable Long rid, @Validated @RequestBody ValidList<FreightCalculatingPostVo> items,
                                   BindingResult bindingResult) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        ReturnObject ret = freightModelService.calculateFreight(rid, items);
        return Common.decorateReturnObject(ret);
    }

    @GetMapping("/freightmodels/{id}/exist")
    public Object existFreightModel(@PathVariable("id") Long id){
        ReturnObject objFeight=freightModelService.getFreightModelById(id);
        return Common.decorateReturnObject(objFeight);
    }

}