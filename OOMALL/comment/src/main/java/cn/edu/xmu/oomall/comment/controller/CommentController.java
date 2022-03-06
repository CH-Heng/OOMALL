package cn.edu.xmu.oomall.comment.controller;
import cn.edu.xmu.oomall.comment.model.vo.CommentConclusionVo;
import cn.edu.xmu.oomall.comment.model.vo.CommentRetVo;
import cn.edu.xmu.oomall.comment.model.vo.CommentVo;
import cn.edu.xmu.oomall.comment.service.CommentService;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 * @date:2021/11/21
 **/
@Api(value = "评论服务", tags = "comment")
@RestController /*Restful的Controller对象*/
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 获得评论所有状态
     */
    @ApiOperation(value = "获得评论的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/comments/states")
    public Object getCommentStates(){
        ReturnObject ret=commentService.getCommentStates();
        return Common.decorateReturnObject(ret);
    }

    /**
     * 买家新增product的评论
     */
    @ApiOperation(value = "买家新增product的评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "CommentVo", name = "commentVo", value = "新增评论", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit(departName = "shops")
    @PostMapping("/internal/products/{id}/comments")
    public Object addCommentOnProduct(
            @PathVariable("id") Long id,
            @Validated @RequestBody CommentVo commentVo,
            BindingResult bindingResult, @LoginUser Long loginUser,@LoginName String loginUserName){

        //校验前端数据评论不能为空
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        var ret = commentService.newComment(id,commentVo, loginUser,loginUserName);
        if(ret.getErrno().equals(ReturnNo.OK))httpServletResponse.setStatus(HttpStatus.CREATED.value());

        return ret;
    }

    /**
     * 查看products评价列表
     */
    @ApiOperation(value = "查看products评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="page", required = false, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="pageSize", required = false, dataType="Integer", paramType="query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/products/{id}/comments")
    public Object selectProductComments(@PathVariable("id") Long id, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize){

        ReturnObject ret=commentService.selectAllPassCommentByProductId(id,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员核审评论
     */
    @ApiOperation(value = "管理员核审评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="did", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })      @ApiImplicitParam(paramType = "body", dataType = "CommentConclusionVo", name = "commentConclusionVo", value = "新增评论", required = true)
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{did}/comments/{id}/confirm")
    public Object confirmComment(@PathVariable("did") Long did, @PathVariable("id") Long id,
                                 @Valid @RequestBody CommentConclusionVo commentConclusionVo,
                                 BindingResult bindingResult, @LoginUser Long loginUser, @LoginName String loginUserName){

        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        ReturnObject ret=commentService.confirmCommnets(did,id,commentConclusionVo,loginUser,loginUserName);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 买家查看自己的评价记录
     */
    @ApiOperation(value = "买家查看自己的评价记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="page", required = false, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="pageSize", required = false, dataType="Integer", paramType="query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/comments")
    public Object getOwnComments(
            @LoginUser Long loginUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize){

        ReturnObject ret=commentService.selectAllCommentsOfUser(loginUser,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员查看未核审的评论列表
     *
     */
    @ApiOperation(value = "管理员查看未核审的评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit(departName = "shops")
    @GetMapping("/shops/{id}/newcomments")
    public Object getAllComments(
            @PathVariable("id") Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize){
        Integer state=0;
        if (id != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject ret=commentService.selectCommentsOfState(id,state,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 商铺管理员查看评论列表
     */
    @ApiOperation(value = "商铺管理员查看评论列表")
    @Audit(departName = "shops")
    @GetMapping("/shops/{id}/comments")
    public Object showShopCommentsByShopId( @PathVariable("id") Long id,@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @LoginUser Long userId, @LoginName String userName){

        ReturnObject ret=commentService.selectAllPassCommentByShopId(id,page,pageSize,userId);
        return Common.decorateReturnObject(ret);
    }
}

