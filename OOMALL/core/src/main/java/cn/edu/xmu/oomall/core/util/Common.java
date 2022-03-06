package cn.edu.xmu.oomall.core.util;

import cn.edu.xmu.oomall.core.model.VoObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * 通用工具类
 * @author Ming Qiu
 **/
public class Common {

    private static Logger logger = LoggerFactory.getLogger(Common.class);

    /**
     * 处理BindingResult的错误
     * @param bindingResult
     * @return
     */
    public static Object processFieldErrors(BindingResult bindingResult, HttpServletResponse response) {
        Object retObj = null;
        if (bindingResult.hasErrors()){
            StringBuffer msg = new StringBuffer();
            //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
            for (FieldError error : bindingResult.getFieldErrors()) {
                msg.append(error.getDefaultMessage());
                msg.append(";");
            }
            logger.debug("processFieldErrors: msg = "+ msg.toString());
            retObj = ResponseUtil.fail(ReturnNo.FIELD_NOTVALID, msg.toString());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return retObj;
    }

    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static ReturnObject getRetObject(ReturnObject<VoObject> returnObject) {
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                VoObject data = returnObject.getData();
                if (data != null){
                    Object voObj = data.createVo();
                    return new ReturnObject(voObj);
                }else{
                    return new ReturnObject();
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * @author xucangbai
     * @param returnObject
     * @param voClass
     * @return
     */
    public static ReturnObject getRetVo(ReturnObject<Object> returnObject,Class voClass) {
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                Object data = returnObject.getData();
                if (data != null){
                    Object voObj = cloneVo(data,voClass);
                    return new ReturnObject(voObj);
                }else{
                    return new ReturnObject();
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     * TODO： 利用cloneVo方法可以生成任意类型v对象,从而把createVo方法从bo中移除
     */

    public static ReturnObject getListRetObject(ReturnObject<List> returnObject) {
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof VoObject) {
                            ret.add(((VoObject)data).createVo());
                        }
                    }
                    return new ReturnObject(ret);
                }else{
                    return new ReturnObject();
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * @author xucangbai
     * @param returnObject
     * @param voClass
     * @return
     */
    public static ReturnObject getListRetVo(ReturnObject<List> returnObject,Class voClass)
    {
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof Object) {
                            ret.add(cloneVo(data,voClass));
                        }
                    }
                    return new ReturnObject(ret);
                }else{
                    return new ReturnObject();
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理分页返回对象
     * @param returnObject 返回的对象
     * @return
     * TODO： 利用cloneVo方法可以生成任意类型v对象,从而把createVo方法从bo中移除
     */
    public static ReturnObject getPageRetObject(ReturnObject<PageInfo<VoObject>> returnObject) {
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                PageInfo<VoObject> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VoObject) {
                            voObjs.add(((VoObject)data).createVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return new ReturnObject(ret);
                }else{
                    return new ReturnObject();
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * @author xucangbai
     * @param returnObject
     * @param voClass
     * @return
     */
    public static ReturnObject getPageRetVo(ReturnObject<PageInfo<Object>> returnObject,Class voClass){
        ReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
            case RESOURCE_FALSIFY:
                PageInfo<Object> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof Object) {
                            voObjs.add(cloneVo(data,voClass));
                        }
                    }
                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return new ReturnObject(code,ret);
                }else{
                    return new ReturnObject(code);
                }
            default:
                return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }



    /**
     * 根据 errCode 修饰 API 返回对象的 HTTP Status
     * @param returnObject 原返回 Object
     * @return 修饰后的返回 Object
     */
    public static Object decorateReturnObject(ReturnObject returnObject) {
        switch (returnObject.getCode()) {
            case RESOURCE_ID_NOTEXIST:
                // 404：资源不存在
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.NOT_FOUND);

            case AUTH_INVALID_JWT:
            case AUTH_JWT_EXPIRED:
                // 401
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.UNAUTHORIZED);

            case INTERNAL_SERVER_ERR:
                // 500：数据库或其他严重错误
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.INTERNAL_SERVER_ERROR);

            case FIELD_NOTVALID:
            case IMG_FORMAT_ERROR:
            case IMG_SIZE_EXCEED:
            case LATE_BEGINTIME:
            case ACT_LATE_PAYTIME:
            case ACT_EARLY_PAYTIME:
            case COUPON_LATE_COUPONTIME:
                // 400
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.BAD_REQUEST);

            case RESOURCE_ID_OUTSCOPE:
            case  FILE_NO_WRITE_PERMISSION:
                // 403
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.FORBIDDEN);

            case OK:
                // 200: 无错误
                Object data = returnObject.getData();
                if (data != null){
                    return ResponseUtil.ok(data);
                }else{
                    return ResponseUtil.ok();
                }

            default:
                data = returnObject.getData();
                if (data != null){
                    return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg(), returnObject.getData());
                }else{
                    return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
                }

        }
    }

    /**
     * 分桶策略
     * @param groupNum
     * @param whole
     * @return
     * @author yujie
     */
    public static int[] getAvgArray(Integer groupNum, Integer whole) {
        // 将数量尽量平均分到多个桶
        int[] incr = new int[groupNum];
        // 数量小于等于组数，随机把数量加到桶中
        Random r = new Random();
        if(whole<=groupNum){
            for(int i=0;i<whole;i++){
                int init = r.nextInt(groupNum);
                incr[init]++;
            }
            return incr;
        }
        // 数量大于组数，先将余数先加到前面的桶中，再将其余相同的增量加到各自随机的桶中
        int unit=whole/groupNum;
        int other=whole-unit*groupNum;
        for (int i = 0; i < groupNum ; i++) {
            if(i<other)
                incr[i]+=unit+1;
            else
            incr[i] += unit;
        }
        return incr;
    }

}
