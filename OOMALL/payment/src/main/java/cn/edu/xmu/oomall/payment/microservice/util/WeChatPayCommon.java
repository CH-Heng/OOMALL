package cn.edu.xmu.oomall.payment.microservice.util;

import cn.edu.xmu.oomall.core.model.VoObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public class WeChatPayCommon {

    public static Object decorateReturnObject(WeChatPayReturnObject returnObject) {

        Object data = returnObject.getData();
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("errmsg", returnObject.getErrmsg());

        switch (returnObject.getCode()) {
            case ORDER_NO_TEXIST:
                // 404
                return new ResponseEntity(obj, HttpStatus.NOT_FOUND);

            case SYSTEM_ERROR:
                // 500
                return new ResponseEntity(obj,HttpStatus.INTERNAL_SERVER_ERROR);

            case ORDER_CLOSED:
            case PARAM_ERROR:
                // 400
                return new ResponseEntity(obj,HttpStatus.BAD_REQUEST);

            case OUT_TRADE_NO_USED:
            case USER_ACCOUNT_ABNORMAL:
                //403
                return new ResponseEntity(obj,HttpStatus.FORBIDDEN);

            case OK:
                // 200
                obj.put("data", data);
                return new ResponseEntity(obj,HttpStatus.OK);

            default:
                obj.put("data", data);
                return obj;
        }
    }

    public static WeChatPayReturnObject getRetObject(WeChatPayReturnObject<VoObject> returnObject) {
        WeChatPayReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
                VoObject data = returnObject.getData();
                if (data != null){
                    Object voObj = data.createVo();
                    return new WeChatPayReturnObject(voObj);
                }else{
                    return new WeChatPayReturnObject();
                }
            default:
                return new WeChatPayReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

}
