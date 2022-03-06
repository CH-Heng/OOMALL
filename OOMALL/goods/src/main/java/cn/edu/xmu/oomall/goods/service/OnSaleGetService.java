package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.OnSaleGetDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleShopVo;
import cn.edu.xmu.oomall.goods.model.bo.OnSaleGetBo;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.vo.OnSaleRetVo;
import cn.edu.xmu.oomall.goods.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/13 02:49
 **/
@Service
public class OnSaleGetService {

    @Autowired
    private OnSaleGetDao onSaleDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ShopService shopService;

    /**
     * 管理员查询特定商品的价格浮动
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectCertainOnsale(Long shopId, Long id,Integer page,Integer pageSize){
        return onSaleDao.selectCertainOnsale(shopId,id,page,pageSize);
    }

    /**
     * 管理员查询特定价格浮动的详情
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectOnsale(Long shopId, Long id){
        ReturnObject returnObject=onSaleDao.selectOnSale(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        OnSaleGetBo onSale=(OnSaleGetBo) returnObject.getData();
        if(onSale.getType().equals(OnSaleGetBo.Type.NOACTIVITY)||onSale.getType().equals(OnSaleGetBo.Type.SECKILL)){
            OnSaleRetVo onSaleRetVo=cloneVo(onSale,OnSaleRetVo.class);
            //设置product字段
            ReturnObject returnObjectProduct=productDao.getProductInfo(onSale.getProductId());
            if(returnObjectProduct.getCode().equals(0)){
                Product product=(Product) returnObjectProduct.getData();
                SimpleProductRetVo simpleProduct=cloneVo(product,SimpleProductRetVo.class);
                onSaleRetVo.setProduct(simpleProduct);
            }
            //设置shop字段
            InternalReturnObject internalObj=shopService.getShopInfo(onSale.getShopId());
            if(internalObj.getErrno().equals(0)) {
                SimpleShopVo simpleShopVo = (SimpleShopVo)internalObj.getData();
                onSaleRetVo.setShop(simpleShopVo);
            }
            return new ReturnObject(onSaleRetVo);
        }else{
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
    }

    /**
     * 查询团购预售活动的所有价格浮动
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectActivities(Long did,Long id, Byte state, LocalDateTime beginTime,
                                         LocalDateTime endTime,Integer page, Integer pageSize){
        return onSaleDao.selectActivities(did,id,state,beginTime,endTime,page,pageSize);
    }

    /**
     * 内部API-查询特定活动的所有价格浮动
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectShareActivities(Long did,Long id,Byte state,Integer page,Integer pageSize){
        return onSaleDao.selectShareActivities(did,id,state,page,pageSize);
    }

    /**
     * 内部API- 查询特定价格浮动的详情，该方法加入redis
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectFullOnsale(Long id){
        ReturnObject returnObject=onSaleDao.selectOnSaleRedis(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        OnSaleGetBo onSale=(OnSaleGetBo) returnObject.getData();
        OnSaleRetVo onSaleRetVo=cloneVo(onSale,OnSaleRetVo.class);
        //设置product字段
        ReturnObject returnObjectProduct=productDao.getProductInfo(onSale.getProductId());
        if(returnObjectProduct.getCode().equals(ReturnNo.OK)){
            Product product=(Product) returnObjectProduct.getData();
            SimpleProductRetVo simpleProduct=cloneVo(product,SimpleProductRetVo.class);
            onSaleRetVo.setProduct(simpleProduct);
        }
        //设置shop字段
        InternalReturnObject internalObj=shopService.getShopInfo(onSale.getShopId());
        if(internalObj.getErrno().equals(0)) {
            SimpleShopVo simpleShopVo = (SimpleShopVo)internalObj.getData();
            onSaleRetVo.setShop(simpleShopVo);
        }
        return new ReturnObject(onSaleRetVo);
    }

    /**
     * 管理员查询所有商品的价格浮动
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectAnyOnsale(Long shopId,Long productId,LocalDateTime beginTime,
                                        LocalDateTime endTime,Integer page,Integer pageSize){
        return onSaleDao.selectAnyOnsale(shopId,productId,beginTime,endTime,page,pageSize);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject isOnsaleExit(Long id){
        ReturnObject returnObject=onSaleDao.selectOnSaleRedis(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)||returnObject.getData()==null) {
            return new ReturnObject(false);
        }else{
            return new ReturnObject(true);
        }
    }

}
