package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.model.bo.ErrorPayment;
import cn.edu.xmu.oomall.payment.model.vo.ErrorPaymentUpdateVo;
import cn.edu.xmu.oomall.payment.service.ReconciliationService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 10:54
 **/
@RestController
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
public class ReconciliationController {
    @Autowired
    private ReconciliationService reconciliationService;
    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 平台管理员查询错帐信息
     */
    @Audit(departName = "payment")
    @GetMapping("shops/{shopId}/erroraccounts")
    public Object getErrorPayment(@LoginUser Long loginUser, @LoginName String loginName,
                                  @PathVariable("shopId")Long shopId, @RequestParam(required = false) String documentId, @RequestParam(required = false) Byte state,
                                  @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                  @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                  @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        if(shopId!=0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime)){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.LATE_BEGINTIME));
        }
        LocalDateTime begin=null,end=null;
        if(beginTime!=null){
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endTime!=null){
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(state!=null&&!state.equals(ErrorPayment.State.PENDING.getCode())&&!state.equals(ErrorPayment.State.SOLVED.getCode())){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.FIELD_NOTVALID));
        }
        ReturnObject returnObject=reconciliationService.getErrorPayment(documentId,state,begin,end,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员查询错帐信息详情
     */
    @Audit(departName = "payment")
    @GetMapping("shops/{shopId}/erroraccounts/{id}")
    public Object getErrorPaymentById(@LoginUser Long loginUser, @LoginName String loginName,
                                      @PathVariable("shopId")Long shopId,  @PathVariable("id")Long id){
        if(shopId!=0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject returnObject=reconciliationService.getErrorPaymentById(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员修改错帐信息
     */
    @Audit(departName = "payment")
    @PutMapping("shops/{shopId}/erroraccounts/{id}")
    public Object updateErrorPayment(@LoginUser Long loginUser, @LoginName String loginName,
                                     @PathVariable("shopId")Long shopId, @PathVariable("id")Long id,
                                     @Validated @RequestBody ErrorPaymentUpdateVo body, BindingResult bindingResult){
        Object object = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != object) {
            return object;
        }
        if(shopId!=0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject returnObject=reconciliationService.updateErrorPayment(id,body,loginUser,loginName);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 对账
     */
    @Audit
    @GetMapping("shops/{id}/reconciliation")
    public Object reconcile(@PathVariable("id")Long id,@LoginUser Long loginUser,@LoginName String loginName,
                            @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                            @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime) throws Exception {
        if(id!=0){
            return Common.decorateReturnObject(new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        LocalDateTime begin=null,end=null;
        if(beginTime!=null){
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        if(endTime!=null){
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        ReturnObject returnObject= reconciliationService.reconcile(begin,end,loginUser,loginName);
        return Common.decorateReturnObject(returnObject);
    }

}
