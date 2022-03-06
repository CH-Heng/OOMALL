package cn.edu.xmu.oomall.activity.service;

import cn.edu.xmu.oomall.activity.dao.ShareActivityDao;
import cn.edu.xmu.oomall.activity.microservice.GoodsService;
import cn.edu.xmu.oomall.activity.microservice.ShopService;
import cn.edu.xmu.oomall.activity.microservice.vo.*;
import cn.edu.xmu.oomall.activity.model.bo.OnSale;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivity;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityBo;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityStatesBo;
import cn.edu.xmu.oomall.activity.model.po.ShareActivityPo;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:55
 */
@Service
public class ShareActivityService {

    @Autowired
    private ShareActivityDao shareActivityDao;

    @Resource
    private GoodsService goodsService;

    @Resource
    private ShopService shopService;

    /**
     * 获得分享活动的所有状态
     *
     * @return ReturnObject
     */
    public ReturnObject getShareState() {
        List<RetStatesVo> list = new ArrayList<>();
        for (ShareActivityStatesBo value : ShareActivityStatesBo.values()) {
            RetStatesVo retStatesVO = new RetStatesVo(value.getCode(), value.getValue());
            list.add(retStatesVO);
        }
        return new ReturnObject(list);
    }

    /**
     * 显示不同（所有/上线）状态的分享活动
     *
     * @param shopId    店铺Id
     * @param productId 货品
     * @param beginTime 晚于此开始时间
     * @param endTime   早于此结束时间
     * @param state     分享活动状态
     * @param page      页码
     * @param pageSize  每页数目
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareByShopId(Long shopId, Long productId, LocalDateTime beginTime,
                                         LocalDateTime endTime, Byte state, Integer page, Integer pageSize) {
        ShareActivityBo bo = new ShareActivityBo();
        bo.setShopId(shopId);
        bo.setBeginTime(beginTime);
        bo.setEndTime(endTime);
        if (state != null) {
            bo.setState(state);
        }
        List<Long> shareActivityIds = new ArrayList<>();
        if (productId != null) {
            //TODO:openfeign获得分享活动id
            InternalReturnObject<PageVo<SimpleOnSaleInfoVo>> onSalesByProductId = goodsService.getOnSales(shopId, productId, null, null, 1, 10);
            if (onSalesByProductId.getErrno()!=0) {
                return new ReturnObject(ReturnNo.getByCode(onSalesByProductId.getErrno()));
            }
            int total = onSalesByProductId.getData().getTotal();
            if (total != 0) {
                onSalesByProductId = goodsService.getOnSales(shopId, productId, null, null, 1, total > 500 ? 500 : total);
                if (onSalesByProductId.getErrno()!=0) {
                    return new ReturnObject(ReturnNo.getByCode(onSalesByProductId.getErrno()));
                }
                List<SimpleOnSaleInfoVo> list = onSalesByProductId.getData().getList();
                for (SimpleOnSaleInfoVo simpleSaleInfoVO : list) {
                    if (simpleSaleInfoVO.getShareActId() != null) {
                        shareActivityIds.add(simpleSaleInfoVO.getShareActId());
                    }
                }
            }else {
                return new ReturnObject(ReturnNo.OK,onSalesByProductId.getErrmsg(),onSalesByProductId.getData());
            }
        }
        ReturnObject shareByShopId = shareActivityDao.getShareByShopId(bo, shareActivityIds, page, pageSize);
        return shareByShopId;
    }


    /**
     * 管理员新增分享活动
     *
     * @param createName      创建者姓名
     * @param createId        创建者id
     * @param shopId          商铺id
     * @param shareActivityVo 新增商铺内容
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addShareAct(String createName, Long createId,
                                    Long shopId, ShareActivityVo shareActivityVo) {
        ShareActivityBo shareActivityBo = cloneVo(shareActivityVo, ShareActivityBo.class);
        setPoCreatedFields(shareActivityBo, createId, createName);
        setPoModifiedFields(shareActivityBo, createId, createName);
        shareActivityBo.setState(ShareActivityStatesBo.DRAFT.getCode());
        shareActivityBo.setShopId(shopId);
        //TODO:通过商铺id弄到商铺名称
        InternalReturnObject<SimpleShopVo> shop = shopService.getShopInfo(shopId);
        if (shop.getErrno()!=0) {
            return new ReturnObject(ReturnNo.getByCode(shop.getErrno()));
        }
        String shopName = shop.getData().getName();
        shareActivityBo.setShopName(shopName);

        ReturnObject returnObject = shareActivityDao.addShareAct(shareActivityBo);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo1 = (ShareActivityBo) returnObject.getData();
        RetShareActivityInfoVo retShareActivityInfoVo = cloneVo(shareActivityBo1, RetShareActivityInfoVo.class);
        retShareActivityInfoVo.setShop(new ShopVo(shareActivityBo1.getShopId(), shareActivityBo1.getShopName()));
        return returnObject;
    }

    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareActivityById(Long id) {
        ReturnObject returnObject = shareActivityDao.getShareActivityById(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo = (ShareActivityBo) returnObject.getData();
        RetShareActivityInfoVo retShareActivityInfoVo = cloneVo(shareActivityBo, RetShareActivityInfoVo.class);
        retShareActivityInfoVo.setShop(new ShopVo(shareActivityBo.getShopId(), shareActivityBo.getShopName()));
        return new ReturnObject(retShareActivityInfoVo);
    }

    /**
     * 查看分享活动详情 只显示上线且在时间内的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getValidShareActivity(Long id) {
        ReturnObject returnObject = shareActivityDao.getShareActivityById(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo = (ShareActivityBo) returnObject.getData();
        if(shareActivityBo.getBeginTime().isAfter(LocalDateTime.now())||shareActivityBo.getEndTime().isBefore(LocalDateTime.now())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject(shareActivityBo);
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getShareActivityByShopIdAndId(Long shopId, Long id) {
        ReturnObject returnObject = shareActivityDao.getShareActivityByShopIdAndId(shopId, id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        ShareActivityBo shareActivityBo = (ShareActivityBo) returnObject.getData();
        RetShareActivitySpecificInfoVo retShareActivitySpecificInfoVo = cloneVo(shareActivityBo, RetShareActivitySpecificInfoVo.class);
        retShareActivitySpecificInfoVo.setShop(new ShopVo(shareActivityBo.getShopId(), shareActivityBo.getShopName()));
        retShareActivitySpecificInfoVo.setCreatedBy(new SimpleUserRetVo(shareActivityBo.getCreatorId(), shareActivityBo.getCreatorName()));
        retShareActivitySpecificInfoVo.setCreatedBy(new SimpleUserRetVo(shareActivityBo.getModifierId(), shareActivityBo.getModifierName()));
        return new ReturnObject(retShareActivitySpecificInfoVo);
    }


    /**
     * 根据ID获取分享活动
     * @param id
     * @return
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject<ShareActivity> getShareActivityByShareActivityId(Long id){
        ReturnObject ret = shareActivityDao.selectShareActivityById(id);
        if (ret.getData()!=null){
            ShareActivity shareActivity = cloneVo((ShareActivityPo)ret.getData(),ShareActivity.class);
            return new ReturnObject<>(shareActivity);
        }
        return ret;
    }

    /**
     * 管理员在已有销售上增加分享
     * @param id OnSale id
     * @param sid 分享活动 id
     * @return OnSale
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject addShareActivityOnOnSale(Long shopId, Long id, Long sid, Long loginUser, String loginUsername){
        InternalReturnObject onSale= goodsService.getOnSaleById(id);
        ReturnObject shareActivity= getShareActivityByShareActivityId(sid);
        if(onSale.getData()==null||shareActivity.getData()==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        FullOnSaleVo onSale1 = (FullOnSaleVo) onSale.getData();
        if(!onSale1.getState().equals(OnSale.State.Online.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ShareActivity shareActivity1=(ShareActivity) shareActivity.getData();
        if(shareActivity1.getState().equals(ShareActivity.State.Offline.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(sid);
        InternalReturnObject updateRet= goodsService.modifyOnSaleShareActId(shopId,id,modifyOnSaleVo);
        if (updateRet.getErrno()!=0){
            return new ReturnObject(updateRet);
        }
        SimpleOnSaleRetVo simpleOnSaleRetVo = (SimpleOnSaleRetVo) Common.cloneVo(onSale1,SimpleOnSaleRetVo.class);
        return new ReturnObject<>(simpleOnSaleRetVo);
    }

    /**
     * 管理员取消已有销售上的分享
     * @param id
     * @param sid
     * @return
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteShareActivityOnOnSale(Long shopId, Long id, Long sid, Long loginUser, String loginUsername){
        InternalReturnObject onSale;
        ReturnObject shareActivity;
        onSale= goodsService.getOnSaleById(id);
        shareActivity= getShareActivityByShareActivityId(sid);
        if(onSale.getData()==null||shareActivity.getData()==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        ModifyOnSaleVo modifyOnSaleVo = new ModifyOnSaleVo();
        modifyOnSaleVo.setShareActId(-1L);
        InternalReturnObject updateRet= goodsService.modifyOnSaleShareActId(shopId,id,modifyOnSaleVo);
        if (updateRet.getErrno()!=0){
            return new ReturnObject(updateRet);
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 管理员修改平台分享活动的内容
     * @param id
     * @param shareActivityVo
     * @return
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject modifyShareActivity(Long id, ShareActivityVo shareActivityVo,Long loginUser, String loginUsername){
        ShareActivityPo shareActivityPo = Common.cloneVo(shareActivityVo,ShareActivityPo.class);
        Common.setPoModifiedFields(shareActivityPo,loginUser,loginUsername);
        shareActivityPo.setId(id);
        var x = getShareActivityByShareActivityId(id).getData();
        if (x==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!x.getState().equals(ShareActivity.State.Draft.getCode().byteValue())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ReturnObject ret = shareActivityDao.modifyShareActivity(shareActivityPo);
        return ret;
    }

    /**
     * 管理员删除草稿状态的分享活动
     * @param id
     * @return
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteShareActivity(Long id,Long loginUser, String loginUsername){
        var x = getShareActivityByShareActivityId(id).getData();
        if (x==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!x.getState().equals(ShareActivity.State.Draft.getCode().byteValue())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ReturnObject ret = shareActivityDao.deleteShareActivity(id);
        return ret;
    }

    /**
     * 根据分享活动id上线分享活动
     * @param id 分享活动id
     * @return 执行结果
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onlineShareActivity(Long id,Long loginUser, String loginUsername){
        var x = getShareActivityByShareActivityId(id).getData();
        if (x==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(x.getState().equals(ShareActivity.State.Online.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ShareActivityPo shareActivityPo = cloneVo(x,ShareActivityPo.class);
        shareActivityPo.setState((byte)1);
        Common.setPoModifiedFields(shareActivityPo,loginUser,loginUsername);
        ReturnObject ret=shareActivityDao.modifyShareActivity(shareActivityPo);
        if (ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        return ret;
    }

    /**
     * 根据分享活动id下线分享活动
     * @param id 分享活动id
     * @return 执行结果
     * @author BingShuai Liu 22920192204245
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offlineShareActivity(Long id,Long loginUser, String loginUsername){
        var x = getShareActivityByShareActivityId(id).getData();
        if (x==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(x.getState().equals(ShareActivity.State.Offline.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        ShareActivityPo shareActivityPo = cloneVo(x,ShareActivityPo.class);
        shareActivityPo.setState((byte)2);
        Common.setPoModifiedFields(shareActivityPo,loginUser,loginUsername);
        ReturnObject ret=shareActivityDao.modifyShareActivity(shareActivityPo);
        return ret;
    }
}
