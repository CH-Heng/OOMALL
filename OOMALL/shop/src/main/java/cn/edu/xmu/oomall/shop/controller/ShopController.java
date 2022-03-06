
package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.model.bo.Shop;
import cn.edu.xmu.oomall.shop.model.vo.ShopConclusionVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopSimpleRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopVo;
import cn.edu.xmu.oomall.shop.service.ShopService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.Depart;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


@Api(value = "店铺", tags = "shop")
@RestController /*Restful的Controller对象*/
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
@Component
public class ShopController {
    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private ShopService shopService;

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "获得店铺简单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping(value = "/shops/{id}")
    public Object getSimpleShopById(@PathVariable("id") Long id){
        ReturnObject ret=shopService.getSimpleShopByShopId(id);
        if(ret.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return Common.decorateReturnObject(ret);
    }
    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员获得店铺信息")
    @GetMapping(value = "/shops/{id}/shops")
    @Audit(departName = "shops")
    public Object getAllShop(@PathVariable Long id,@RequestParam(required = false)Integer page,@RequestParam(required = false)Integer pageSize){
        if(id != 0){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=shopService.getAllShop(page,pageSize);
        return Common.decorateReturnObject(ret);
    }


    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "获得店铺的所有状态")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功") })
    @GetMapping(value = "/shops/states")
    public Object getshopState()
    {
        ReturnObject<List> returnObject=shopService.getShopStates();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "店家申请店铺")
    @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header")
    @ApiResponses(value = {
            @ApiResponse(code = 969, message = "用户已经有店铺"),
            @ApiResponse(code = 200, message = "成功") })
    @Audit(departName = "shops")
    @PostMapping(value = "/shops")
    public Object addShop(@Validated @RequestBody ShopVo shopvo, BindingResult bindingResult, @Depart Long shopid, @LoginUser Long loginUser, @LoginName String loginUsername){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }

        if(shopid.equals(-1L))
        {
            var ret = shopService.newShop(shopvo,loginUser,loginUsername);
            if(ret.getCode().equals(ReturnNo.OK)) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
            }
            return Common.decorateReturnObject(ret);
        }
        else
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.SHOP_USER_HASSHOP, "您已经拥有店铺，无法重新申请"));

    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "店家修改店铺信息", nickname = "modifyShop", notes = "", tags={ "shop", })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 507, message = "该店铺无法修改")})
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{id}")
    public Object modifyShop(@Validated @RequestBody ShopVo shopVo,BindingResult bindingResult,@PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        else
        {
            ReturnObject ret=shopService.updateShop(id,shopVo, loginUser,loginUsername);
            return Common.decorateReturnObject(ret);
        }
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员或店家关闭店铺", nickname = "deleteShop", notes = "如果店铺从未上线则物理删除",  tags={ "shop", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功") ,
            @ApiResponse(code = 180, message = "该店铺无法被执行关闭操作")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "shopToken", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "商店id", required = true, dataType = "Long", paramType = "path")
    })
    @DeleteMapping(value = "/shops/{id}")
    @Audit(departName = "shops")
    public Object deleteShop(@ApiParam(value = "shop ID",required=true) @PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){

        ReturnObject returnObject = shopService.getShopByShopId(id);
        if (returnObject.getCode()!=ReturnNo.OK){
            return Common.decorateReturnObject(returnObject);
        }
        Shop shop = (Shop) returnObject.getData();
        if(shop.getState() == Shop.State.OFFLINE.getCode().byteValue())
        {
            ReturnObject ret=shopService.deleteShopById(id, loginUser,loginUsername);
            return Common.decorateReturnObject(ret);
        }
        else
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.STATENOTALLOW));
        }
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "平台管理员审核店铺信息", nickname = "shopsShopIdNewshopsIdAuditPut", notes = "",  tags={ "shop", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功") ,
            @ApiResponse(code = 150, message = "该店铺不是待审核状态")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{shopId}/newshops/{id}/audit")
    public Object auditShop(@LoginUser Long loginUser,@LoginName String loginUsername,@PathVariable("shopId") Long shopId,@PathVariable("id") Long id,@RequestBody ShopConclusionVo conclusion){

        ReturnObject objShop = shopService.getShopByShopId(id);
        if(!objShop.getCode().equals(ReturnNo.OK)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Common.decorateReturnObject(objShop);
        }
        Shop shop=(Shop) objShop.getData();
        if(shop.getState() == Shop.State.EXAME.getCode().byteValue())
        {
            ReturnObject ret=shopService.passShop(id,conclusion,loginUser,loginUsername);
            return Common.decorateReturnObject(ret);
        }
        else
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo. STATENOTALLOW));
        }
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员上线店铺", nickname = "shopsIdOnshelvesPut", notes = "", tags={ "shop", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 160, message = "该店铺无法上线")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @Audit(departName = "shops")
    @PutMapping(value = "/shops/{id}/online")
    public Object shopsIdOnshelvesPut(@PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){

        ReturnObject ret= shopService.onShelfShop(id, loginUser,loginUsername);
        if(ret.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return Common.decorateReturnObject(ret);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @ApiOperation(value = "管理员下线店铺", nickname = "shopsIdOffshelvesPut", notes = "", tags={ "shop", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 170, message = "该店铺无法下线")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "adminToken", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/shops/{id}/offline")
    @Audit(departName = "shops")
    public Object shopsIdOffshelvesPut(@PathVariable("id") Long id,@LoginUser Long loginUser,@LoginName String loginUsername){

        ReturnObject ret= shopService.offShelfShop(id,loginUser,loginUsername);
        if(ret.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return Common.decorateReturnObject(ret);
    }
    /**
     * @author
     * @date 2021/12/18
     */
    @ApiOperation(value = "管理员获得店铺信息")
    @GetMapping(value = "internal/shops/all")
    public Object getShop(@RequestParam(required = false)Integer page,
                          @RequestParam(required = false)Integer pageSize){
        ReturnObject ret= shopService.getAllShop(page,pageSize);
        Map<String, Object> map = (Map<String, Object>) ret.getData();
        List list = (List) map.get("list");
        List<ShopSimpleRetVo> l = new ArrayList<>();
        for(Object o:list){
            l.add(cloneVo(o,ShopSimpleRetVo.class));
        }
        return new InternalReturnObject<>(l);
    }
}
