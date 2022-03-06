package cn.edu.xmu.oomall.activity.util;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleOnSaleInfoVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/13 15:00
 */
public class CreateObject {
    public static InternalReturnObject createOnSaleInfoDTO(Long id) {
        if(id<=0){
            return new InternalReturnObject();
        }
        List<SimpleOnSaleInfoVo> list = new ArrayList<>();
        SimpleOnSaleInfoVo simpleSaleInfoVO = new SimpleOnSaleInfoVo();
        simpleSaleInfoVO.setShareActId(1l);
        list.add(simpleSaleInfoVO);
        SimpleOnSaleInfoVo simpleSaleInfoVo2 = new SimpleOnSaleInfoVo();
        simpleSaleInfoVo2.setShareActId(2l);
        list.add(simpleSaleInfoVo2);
        SimpleOnSaleInfoVo simpleSaleInfoVo3 = new SimpleOnSaleInfoVo();
        simpleSaleInfoVo3.setShareActId(3l);
        list.add(simpleSaleInfoVo3);
        //模拟不是share活动
        SimpleOnSaleInfoVo simpleSaleInfoVo5 = new SimpleOnSaleInfoVo();
        list.add(simpleSaleInfoVo5);
        SimpleOnSaleInfoVo simpleSaleInfoVo4 = new SimpleOnSaleInfoVo();
        simpleSaleInfoVo4.setShareActId(4l);
        list.add(simpleSaleInfoVo4);
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",10);
        return new InternalReturnObject(map);
    }

    public static InternalReturnObject<SimpleShopVo> createShopInfoDTO(Long id) {
        if(id<=0){
            return new InternalReturnObject(504,"不存在该商铺");
        }
        return new InternalReturnObject<>(new SimpleShopVo(id,"良耳的商铺"));
    }

}
