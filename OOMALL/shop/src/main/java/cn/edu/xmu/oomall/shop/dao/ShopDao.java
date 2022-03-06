package cn.edu.xmu.oomall.shop.dao;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.mapper.ShopPoMapper;
import cn.edu.xmu.oomall.shop.model.bo.Shop;
import cn.edu.xmu.oomall.shop.model.po.ShopPo;
import cn.edu.xmu.oomall.shop.model.po.ShopPoExample;
import cn.edu.xmu.oomall.shop.model.vo.ShopRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class ShopDao {
    @Autowired
    ShopPoMapper shopPoMapper;


    public ReturnObject<Shop> getShopById(Long id) {
        ShopPo shopPo=new ShopPo();
        try {
            shopPo = shopPoMapper.selectByPrimaryKey(id);
            if(shopPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            Shop shop = (Shop) cloneVo(shopPo, Shop.class);
            return new ReturnObject<>(shop);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    public ReturnObject getAllShop(Integer page, Integer pageSize) {
        ShopPoExample example = new ShopPoExample();
        List<ShopPo> shopPos;
        try {
            PageHelper.startPage(page, pageSize);
            shopPos = shopPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(shopPos);
            ReturnObject ret = Common.getPageRetVo(new ReturnObject<>(pageInfo), ShopRetVo.class);
            return ret;
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }


    public ReturnObject getShopState() {
        List<Map<String, Object>> stateList = new ArrayList<>();
        for (Shop.State states : Shop.State.values()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("code", states.getCode());
            temp.put("name", states.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }


    public ReturnObject newShop(ShopPo po) {
        int ret;
        po.setDeposit(Long.valueOf(0));
        po.setState(Shop.State.EXAME.getCode().byteValue());
        try {
            ret = shopPoMapper.insertSelective(po);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            } else {
                return new ReturnObject(po);
            }
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /**
     * 功能描述: 商家修改店铺信息
     *
     * @Param: [po]
     */
    public ReturnObject UpdateShop(Long id, Shop shop) {
        int ret;
        try {
            ShopPo shopPo = shopPoMapper.selectByPrimaryKey(id);
            if (shopPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            shopPo.setName(shop.getName());
            if (shopPo.getState() == Shop.State.FORBID.getCode().byteValue()) {
                return new ReturnObject(ReturnNo.STATENOTALLOW, "商铺处于关闭态");
            }
            shopPo.setGmtModified(LocalDateTime.now());
            ret = shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if (ret == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        } else {
            return new ReturnObject();
        }
    }


    public ReturnObject updateShopState(Shop shop) {
        ShopPo shopPo = (ShopPo) cloneVo(shop, ShopPo.class);
        int ret;
        try {
            ret = shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if (ret == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        } else {
            return new ReturnObject();
        }
    }
}
