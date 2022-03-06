package cn.edu.xmu.oomall.share.service;


import cn.edu.xmu.oomall.share.dao.ShareDao;
import cn.edu.xmu.oomall.share.microservice.GoodsService;
import cn.edu.xmu.oomall.share.microservice.ActivityService;
import cn.edu.xmu.oomall.share.microservice.vo.*;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.SuccessfulShare;
import cn.edu.xmu.oomall.share.model.po.SharePo;
import cn.edu.xmu.oomall.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.oomall.share.model.vo.ShareRetVo;
import cn.edu.xmu.oomall.share.model.vo.SimpleObjectVo;
import cn.edu.xmu.oomall.share.model.vo.SimpleSuccessfulShareRetVo;
import cn.edu.xmu.oomall.share.model.vo.SuccessfulShareRetVo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

@Service
public class ShareService {
    @Autowired
    private ShareDao shareDao;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ActivityService activityService;

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject share(Long onsaleId, Long loginUserId, String loginUserName) {
        InternalReturnObject onsaleRet = goodsService.selectFullOnsale(onsaleId);
        OnsaleRetVo onsaleRetVo;
        if (onsaleRet.getErrno() == 0) {
            onsaleRetVo = cloneVo(onsaleRet.getData(),OnsaleRetVo.class) ;
        } else {
            return new ReturnObject(ReturnNo.getByCode(onsaleRet.getErrno()),onsaleRet.getErrmsg());
        }

        if (onsaleRetVo.getShareActId()==null){
            return new ReturnObject(ReturnNo.SHARE_UNSHARABLE);
        }

        InternalReturnObject shareActRet=null;
        //是否存在有效的分享活动
        try {
            shareActRet=activityService.getShareActivityById(onsaleRetVo.getShareActId());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

        if (shareActRet.getErrno()!=0){
            return new ReturnObject(ReturnNo.SHARE_UNSHARABLE);
        }

        Share share = new Share();
        share.setSharerId(loginUserId);
        share.setOnsaleId(onsaleId);
        share.setQuantity(0L);
        share.setShareActId(onsaleRetVo.getShareActId());
        share.setState((byte)0);
        share.setProductId(onsaleRetVo.getProduct().getId());
        setPoCreatedFields(share, loginUserId, loginUserName);
        ReturnObject returnObject = shareDao.insertShare(share);
        if (returnObject.getCode() == ReturnNo.OK) {
            ShareRetVo shareRetVo = (ShareRetVo) returnObject.getData();
            shareRetVo.setProduct(onsaleRetVo.getProduct());
        }
        return returnObject;
    }

    @Transactional(readOnly = true)
    public ReturnObject getShares(Long shopId,Long productId, LocalDateTime beginTime, LocalDateTime endTime,Long loginUserId, Integer page, Integer pageSize) {
        if (shopId!=null){
            ReturnObject productRet = goodsService.getProductDetails(productId);
            if (!(productRet.getCode()==ReturnNo.OK)) {
                return productRet;
            }
            ProductRetVo productRetVo =cloneVo(productRet.getData(),ProductRetVo.class);
            if (!productRetVo.getShop().getId().equals(shopId)) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "该商品不属于该商铺");
            }
        }

        ReturnObject<PageInfo> returnObject=shareDao.getShares(productId, beginTime, endTime, loginUserId, page, pageSize);

        List<ShareRetVo> shareRetVoList=new ArrayList<>();
        if (returnObject.getCode() == ReturnNo.OK) {
            PageInfo sharePoPageInfo=returnObject.getData();
            for (Object o:sharePoPageInfo.getList()){
                SharePo sharePo=cloneVo(o,SharePo.class);
                ShareRetVo shareRetVo=cloneVo(o,ShareRetVo.class);
                shareRetVo.setSharer(new SimpleObjectVo(sharePo.getCreatorId(),sharePo.getCreatorName()));
                shareRetVo.setCreator(new SimpleObjectVo(sharePo.getCreatorId(),sharePo.getCreatorName()));
                shareRetVo.setModifier(new SimpleObjectVo(sharePo.getModifierId(),sharePo.getModifierName()));

                ReturnObject<ProductRetVo> productRet=goodsService.getProductDetails(sharePo.getProductId());
                if (productRet.getCode()==ReturnNo.OK){
                    ProductRetVo productRetVo=productRet.getData();
                    SimpleProductRetVo simpleProductRetVo=new SimpleProductRetVo(productRetVo.getId(),productRetVo.getName(),productRetVo.getImageUrl());
                    shareRetVo.setProduct(simpleProductRetVo);
                }else {
                    return productRet;
                }
                shareRetVoList.add(shareRetVo);
            }
        }

        PageInfo pageInfo=returnObject.getData();
        pageInfo.setList(shareRetVoList);

        return Common.getPageRetVo(new ReturnObject(pageInfo),ShareRetVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getShareProduct(Long shareId, Long productId,Long loginUserId,String loginUserName) {
        SuccessfulShare successfulShare=new SuccessfulShare();
        ReturnObject shareRet=shareDao.getShareById(shareId);
        if (shareRet.getCode()!=ReturnNo.OK){
            return shareRet;
        }
        SharePo sharePo= (SharePo) shareRet.getData();
        successfulShare.setShareId(shareId);
        successfulShare.setSharerId(sharePo.getSharerId());
        successfulShare.setOnsaleId(sharePo.getOnsaleId());
        successfulShare.setProductId(productId);
        successfulShare.setCustomerId(loginUserId);
        successfulShare.setState((byte)0);
        setPoCreatedFields(successfulShare,loginUserId,loginUserName);
        ReturnObject returnObject=shareDao.insertSuccessfulShare(successfulShare);
        if (returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        ReturnObject<ProductRetVo> productRet=goodsService.getProductDetails(productId);
        return productRet;
    }


    @Transactional(readOnly = true)
    public ReturnObject getSuccessfulShares(Long productId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize, Long loginUserId) {
        SuccessfulShare successfulShare = new SuccessfulShare();
        successfulShare.setProductId(productId);
        ReturnObject<PageInfo> returnObject = shareDao.getSuccessfulShares(successfulShare, beginTime, endTime, loginUserId, page, pageSize);

        List<SuccessfulShareRetVo> successfulShareRetVos = new ArrayList<>();
        if (returnObject.getCode() != ReturnNo.OK) {
            return returnObject;
        }

        PageInfo pageInfo = returnObject.getData();
        for (Object o : pageInfo.getList()) {
            SuccessfulSharePo successfulSharePo = (SuccessfulSharePo) o;
            SuccessfulShareRetVo successfulShareRetVo = cloneVo(successfulSharePo, SuccessfulShareRetVo.class);
            if (successfulSharePo.getProductId() == null) {
                continue;
            }
            ReturnObject productRet = goodsService.getProductDetails(successfulSharePo.getProductId());
            if (!(productRet.getCode() == ReturnNo.OK)) {
                return productRet;
            }
            ProductRetVo productRetVo = cloneVo(productRet.getData(), ProductRetVo.class);
            SimpleProductRetVo simpleProductRetVo = new SimpleProductRetVo(productRetVo.getId(), productRetVo.getName(), productRetVo.getImageUrl());
            successfulShareRetVo.setProduct(simpleProductRetVo);
            successfulShareRetVos.add(successfulShareRetVo);
        }
        pageInfo.setList(successfulShareRetVos);
        return Common.getPageRetVo(new ReturnObject(pageInfo), SuccessfulShareRetVo.class);
    }

    /**
     * 管理查询分享成功记录
     * @param shopId
     * @param productId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getProductSuccessfulShares(Long shopId, Long productId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        ReturnObject productRet = goodsService.getProductDetails(productId);
        if (!(productRet.getCode() == ReturnNo.OK)) {
            return productRet;
        }
        ProductRetVo productRetVo = cloneVo(productRet.getData(),ProductRetVo.class);
        if (!productRetVo.getShop().getId().equals(shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "该商品不属于该商铺");
        }

        SuccessfulShare successfulShare = new SuccessfulShare();
        successfulShare.setProductId(productId);
        ReturnObject<PageInfo> returnObject = shareDao.getSuccessfulShares(successfulShare, beginTime, endTime, null, page, pageSize);

        List<SuccessfulShareRetVo> successfulShareRetVos = new ArrayList<>();
        if (returnObject.getCode() == ReturnNo.OK) {
            PageInfo<Object> successfulSharePoPageInfo = returnObject.getData();
            for (Object o : successfulSharePoPageInfo.getList()) {
                SuccessfulSharePo successfulSharePo = (SuccessfulSharePo) o;
                SuccessfulShareRetVo successfulShareRetVo = cloneVo(successfulSharePo, SuccessfulShareRetVo.class);

                SimpleProductRetVo simpleProductRetVo = new SimpleProductRetVo(productRetVo.getId(), productRetVo.getName(), productRetVo.getImageUrl());
                successfulShareRetVo.setProduct(simpleProductRetVo);
                successfulShareRetVos.add(successfulShareRetVo);
            }

            PageInfo pageInfo=returnObject.getData();
            pageInfo.setList(successfulShareRetVos);

            return Common.getPageRetVo(new ReturnObject(pageInfo),SuccessfulShareRetVo.class);
            //ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<>(successfulShareRetVos));
            //return Common.getPageRetVo(new ReturnObject(PageInfo), SuccessfulShareRetVo.class);
        } else {
            return returnObject;
        }
    }

    /**
     * 根据onsaleId和customerId找最早的未清算的successfulshare
     * @param onsaleId
     * @param customerId
     * @return
     */
    @Transactional(readOnly = true)
    public InternalReturnObject getEarliestSuccessfulShare(Long onsaleId,Long customerId){
         SuccessfulShare successfulShare=new SuccessfulShare();
        successfulShare.setOnsaleId(onsaleId);
        successfulShare.setCustomerId(customerId);
        successfulShare.setState((byte)0);
        ReturnObject sShareRet=shareDao.getSuccessfulShares(successfulShare,null,null,null,null,null);
        if (!(sShareRet.getCode()==ReturnNo.OK)){
            return new InternalReturnObject(sShareRet.getCode().getCode(),sShareRet.getCode().getMessage());
        }
        List<SuccessfulSharePo> successfulSharePoList=(List<SuccessfulSharePo>)sShareRet.getData();

        if (successfulSharePoList.size()==0){
            return new InternalReturnObject(ReturnNo.OK.getCode(),ReturnNo.OK.getMessage());
        }

        //找有效的未清算分享成功记录
        for (SuccessfulSharePo successfulSharePo:successfulSharePoList){
            if (!successfulSharePo.getState().equals((byte)0)){
                successfulSharePoList.remove(successfulSharePo);
            }
        }

        //找最早且未清算的分享成功记录
        SuccessfulSharePo successfulSharePo=Collections.min(successfulSharePoList, Comparator.comparing(SuccessfulSharePo::getGmtCreate));

        ReturnObject shareRet=shareDao.getShareById(successfulSharePo.getShareId());
        if (!(shareRet.getCode()==ReturnNo.OK)){
            return new InternalReturnObject(shareRet.getCode().getCode(),shareRet.getCode().getMessage());
        }
        SharePo sharePo=(SharePo) shareRet.getData();

        InternalReturnObject shareActRet= activityService.getShareActivityById(sharePo.getShareActId());
        if (shareActRet.getErrno()!=0||shareActRet.getData()==null){
            return shareActRet;
        }
        ShareActivityRetVo shareActivityRetVo =cloneVo(shareActRet.getData(),ShareActivityRetVo.class);

        SimpleSuccessfulShareRetVo simpleSuccessfulShareRetVo =new SimpleSuccessfulShareRetVo(sharePo.getId(),sharePo.getSharerId(), shareActivityRetVo.getStrategy());
        return new InternalReturnObject(simpleSuccessfulShareRetVo);
    }


    @Transactional(rollbackFor = Exception.class)
    public InternalReturnObject setStateliquidated(Long id) {
        ReturnObject returnObject = shareDao.setSuccessfulShareState(id,(byte)1);
        return new InternalReturnObject(returnObject.getCode().getCode(),returnObject.getErrmsg(),returnObject.getData());
    }
}
