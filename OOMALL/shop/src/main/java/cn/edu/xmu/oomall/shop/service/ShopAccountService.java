package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.dao.ShopAccountDao;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


/**
 * @author  Xusheng Wang
 * @date  2021-11-11
 * @studentId 34520192201587
 */

@Service
public class ShopAccountService {

    @Autowired
    private ShopAccountDao shopAccountDao;

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public ReturnObject addShopAccount(ShopAccountVo shopAccountVo, Long shopId,Long loginUserId,String loginUserName) {        ShopAccountPo shopAccountPo = cloneVo(shopAccountVo,ShopAccountPo.class);
        ReturnObject ret = shopAccountDao.addShopAccount(shopAccountPo,shopId,loginUserId,loginUserName);
        if (ret.getCode()!=ReturnNo.OK) {
            return ret;
        }
        ShopAccountRetVo shopAccountVo1=cloneVo(shopAccountPo,ShopAccountRetVo.class);
        return new ReturnObject<>(shopAccountVo1);
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public ReturnObject getShopAccounts(Long shopId) {
        ReturnObject returnObject=shopAccountDao.getShopAccounts(shopId);
        return returnObject;
    }

    /**
     * @author  Xusheng Wang
     * @date  2021-11-11
     * @studentId 34520192201587
     */
    public ReturnObject deleteAccount(Long shopId, Long accountId) {
        if(!shopAccountDao.checkShopAccount(shopId,accountId)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"账户信息有误！");
        }
        if(shopAccountDao.deleteAccount(accountId)){
            return new ReturnObject<>();
        }
        else {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, "删除失败！");
        }
    }
}
