package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.microservice.PaymentService;
import cn.edu.xmu.oomall.shop.microservice.PrivilegeService;
import cn.edu.xmu.oomall.shop.microservice.ReconciliationService;
import cn.edu.xmu.oomall.shop.microservice.vo.RefundDepositVo;
import cn.edu.xmu.oomall.shop.model.bo.Shop;
import cn.edu.xmu.oomall.shop.model.po.ShopAccountPo;
import cn.edu.xmu.oomall.shop.model.po.ShopPo;
import cn.edu.xmu.oomall.shop.model.vo.ShopAllRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopConclusionVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopSimpleRetVo;
import cn.edu.xmu.oomall.shop.model.vo.ShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

@Service
public class ShopService {
    @Autowired
    private ShopDao shopDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<Shop> getShopByShopId(Long ShopId) {
        return shopDao.getShopById(ShopId);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getAllShop(Integer page, Integer pageSize) {
        ReturnObject ret = shopDao.getAllShop(page, pageSize);
        return ret;
    }


    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getSimpleShopByShopId(Long ShopId) {
        ReturnObject ret = shopDao.getShopById(ShopId);
        if(!ret.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        ShopSimpleRetVo vo = (ShopSimpleRetVo) cloneVo(ret.getData(), ShopSimpleRetVo.class);
        return new ReturnObject(vo);
    }

    /**
     * @Author: 蒋欣雨
     * @Sn: 22920192204219
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject newShop(ShopVo shopVo, Long loginUser, String loginUsername) {
        ShopPo po = new ShopPo();
        po.setName(shopVo.getName());
        setPoCreatedFields(po, loginUser, loginUsername);
        ReturnObject ret = shopDao.newShop(po);
        if(ret.getCode()==ReturnNo.OK){
            ShopPo shopPo = (ShopPo)ret.getData();
            InternalReturnObject updatePriObj = privilegeService.addToDepart(loginUser,shopPo.getId());
            if (updatePriObj.getErrno()!=0){
                return new ReturnObject(updatePriObj);
            }
            ShopSimpleRetVo vo = (ShopSimpleRetVo) cloneVo(ret.getData(), ShopSimpleRetVo.class);
            ret = new ReturnObject(vo);
        }
        return ret;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getShopStates() {
        return shopDao.getShopState();
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateShop(Long id, ShopVo shopVo, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id);
        shop.setName(shopVo.getName());
        setPoModifiedFields(shop, loginUser, loginUsername);

        ReturnObject ret = shopDao.UpdateShop(shop.getId(), shop);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteShopById(Long id, Long loginUser, String loginUsername) {
//        InternalReturnObject ret = reconciliationService.isClean(id);
//        if (!ret.getErrno().equals(0)) {
//            return new ReturnObject(ReturnNo.getByCode(ret.getErrno()),ret.getErrmsg());
//        }
//
//        Boolean result = (Boolean) ret.getData();
//        if (!result) {
//            //商铺尚未完成清算
//            return new ReturnObject(ReturnNo.SHOP_NOT_RECON);
//        }
//
//        //商铺已完成清算
//        /*****************************************/
//        //TODO:需要调用ShopAccountDao获得
//        ShopAccountPo accountPo = new ShopAccountPo();
//        accountPo.setAccount("11111111");
//        accountPo.setType((byte) 0);
//        accountPo.setName("测试");
//        /*****************************************/
//        RefundDepositVo depositVo = (RefundDepositVo) cloneVo(accountPo, RefundDepositVo.class);
//        InternalReturnObject refundRet = paymentService.refund(depositVo);
//        if (!refundRet.getErrno().equals(0)) {
//            return new ReturnObject(ReturnNo.getByCode(refundRet.getErrno()),refundRet.getErrmsg());
//        }

        Shop shop = new Shop();
        shop.setId(id.longValue());
        shop.setState(Shop.State.FORBID.getCode().byteValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        ReturnObject retUpdate = shopDao.updateShopState(shop);
        return retUpdate;

    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject passShop(Long id, ShopConclusionVo conclusion, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id.longValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(conclusion.getConclusion() == true ? Shop.State.OFFLINE.getCode().byteValue() : Shop.State.EXAME.getCode().byteValue());
        ReturnObject ret = shopDao.updateShopState(shop);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject onShelfShop(Long id, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id);
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(Shop.State.ONLINE.getCode().byteValue());
        ReturnObject objShop = getShopByShopId(id);
        if(!objShop.getCode().equals(ReturnNo.OK)){
            return objShop;
        }
        Shop shop1=(Shop) objShop.getData();
        if (shop1.getState() == Shop.State.OFFLINE.getCode().byteValue()) {
            ReturnObject ret = shopDao.updateShopState(shop);
            return ret;
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ReturnObject offShelfShop(Long id, Long loginUser, String loginUsername) {
        Shop shop = new Shop();
        shop.setId(id.longValue());
        setPoModifiedFields(shop, loginUser, loginUsername);
        shop.setState(Shop.State.OFFLINE.getCode().byteValue());
        ReturnObject objShop = getShopByShopId(id);
        if(!objShop.getCode().equals(ReturnNo.OK)){
            return objShop;
        }
        Shop shop1=(Shop) objShop.getData();
        if (shop1.getState() == Shop.State.ONLINE.getCode().byteValue()) {
            ReturnObject ret = shopDao.updateShopState(shop);
            return ret;
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }
}