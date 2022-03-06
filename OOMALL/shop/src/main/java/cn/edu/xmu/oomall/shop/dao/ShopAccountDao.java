package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.mapper.ShopAccountPoMapper;
import cn.edu.xmu.oomall.shop.mapper.ShopPoMapper;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPoExample;
import cn.edu.xmu.oomall.shop.model.po.ShopPo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountRetVo;
import cn.edu.xmu.oomall.shop.model.vo.SimpleAdminUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 * @studentId 34520192201587
 */

@Repository
public class ShopAccountDao {

    @Autowired(required = false)
    public ShopAccountPoMapper shopAccountPoMapper;

    @Autowired
    public ShopPoMapper shopPoMapper;

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public ReturnObject getShopAccounts(Long shopId) {
        List<ShopAccountPo> accountPoList= new ArrayList<>();
        ShopAccountPoExample shopAccountPoExample=new ShopAccountPoExample();
        ShopAccountPoExample.Criteria criteria=shopAccountPoExample.createCriteria();
        if (shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        try {
            ShopPo shopPo = shopPoMapper.selectByPrimaryKey(shopId);
            if (shopPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            accountPoList = shopAccountPoMapper.selectByExample(shopAccountPoExample);
            List<ShopAccountRetVo> ret= new ArrayList<>();
            for(ShopAccountPo shopAccountPo:accountPoList){
                ShopAccountRetVo shopAccountRetVo = cloneVo(shopAccountPo,ShopAccountRetVo.class);
                shopAccountRetVo.setCreator(new SimpleAdminUserVo(shopAccountPo.getCreatorId(),shopAccountPo.getCreatorName()));
                shopAccountRetVo.setModifier(new SimpleAdminUserVo(shopAccountPo.getModifierId(),shopAccountPo.getModifierName()));
                ret.add(shopAccountRetVo);
            }
            return new ReturnObject<>(ret);
        }
        catch (Exception exception){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,exception.getMessage());
        }
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public ReturnObject addShopAccount(ShopAccountPo shopAccountPo,Long shopId,Long loginUserId,String loginUserName) {
        //Po添加设置基本信息
        setPoCreatedFields(shopAccountPo,loginUserId,loginUserName);
        ShopAccountPoExample shopAccountPoExample=new ShopAccountPoExample();
        ShopAccountPoExample.Criteria criteria=shopAccountPoExample.createCriteria();
        criteria.andPriorityEqualTo(shopAccountPo.getPriority());
        try {
            //判断shopId是否存在
            if(shopId!=null){
                ShopPo shopPo = shopPoMapper.selectByPrimaryKey(shopId);
                if (shopPo==null){
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
                criteria.andShopIdEqualTo(shopId);
                shopAccountPo.setShopId(shopId);
            }
            //当优先级没有相同的时候直接插入
            List<ShopAccountPo> shopAccountPos = shopAccountPoMapper.selectByExample(shopAccountPoExample);
            if (shopAccountPos.isEmpty()){
                int isInserted = shopAccountPoMapper.insertSelective(shopAccountPo);
                if (isInserted!=1){
                    return new ReturnObject(ReturnNo.FIELD_NOTVALID);
                }
                return new ReturnObject();
            }
            //优先级相同时，使优先级小于等于插入账户的账户的优先级都降低一级
            else {
                ShopAccountPoExample shopAccountPoExample1 = new ShopAccountPoExample();
                ShopAccountPoExample.Criteria criteria1 = shopAccountPoExample1.createCriteria();
                criteria1.andPriorityGreaterThanOrEqualTo(shopAccountPo.getPriority());
                if (shopId != null) {
                    criteria1.andShopIdEqualTo(shopId);
                }
                //选出优先级小于等于插入账户的账户列表
                List<ShopAccountPo> lowerPriorityShopAccountPo = shopAccountPoMapper.selectByExample(shopAccountPoExample1);
                lowerPriorityShopAccountPo.stream().forEach(shopAccountPo1 -> {
                    shopAccountPo1.setPriority((byte) (shopAccountPo1.getPriority() + 1));//使其priority+1,即使其优先级降低
                    shopAccountPoMapper.updateByPrimaryKey(shopAccountPo1);
                });
                int isInserted = shopAccountPoMapper.insertSelective(shopAccountPo);
                if (isInserted!=1){
                    return new ReturnObject(ReturnNo.FIELD_NOTVALID);
                }
                return new ReturnObject();
            }
        }
        catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public boolean deleteAccount(Long accountId) {
        try {
            return shopAccountPoMapper.deleteByPrimaryKey(accountId)==1;
        }
        catch (Exception exception){
            return false;
        }
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public boolean checkShopAccount(Long shopId, Long accountId) {
        try {
            return shopId==shopAccountPoMapper.selectByPrimaryKey(accountId).getShopId();
        }
        catch (Exception exception){
            return false;
        }
    }
}
