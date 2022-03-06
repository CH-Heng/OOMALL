package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.GoodsDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.vo.CreateGoodsVo;
import cn.edu.xmu.oomall.goods.model.vo.GoodsRetVo;
import cn.edu.xmu.oomall.goods.model.vo.GoodsVo;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ProductDao productDao;
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject insertGoods(Long shopId, GoodsVo goodsVo, Long loginUserId, String loginUserName)
    {
        Goods goods=cloneVo(goodsVo,Goods.class);
        setPoCreatedFields(goods,loginUserId,loginUserName);
        goods.setShopId(shopId);
        ReturnObject ret=goodsDao.createNewGoods(goods);
        if(ret.getCode().equals(ReturnNo.OK)){
            CreateGoodsVo goodsVo1=(CreateGoodsVo)cloneVo(ret.getData(),CreateGoodsVo.class);
            return new ReturnObject(goodsVo1);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject deleteGoods(Long shopId,Long id)
    {
        Long defaultValueWhenDel=0L;
        productDao.resetGoodsIdForProducts(id,defaultValueWhenDel);
        return new ReturnObject(goodsDao.deleteGoodsById(shopId,id));
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject updateGoods(Long shopId, Long id, GoodsVo goodsVo, Long loginUser, String loginUserName)
    {
        Goods goods = cloneVo(goodsVo,Goods.class);
        goods.setId(id);
        goods.setShopId(shopId);
        setPoModifiedFields(goods,loginUser,loginUserName);
        return goodsDao.updateGoods(goods);
    }

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject searchById(Long shopId,Long id) {
        ReturnObject<Goods> ret = goodsDao.searchGoodsById(shopId, id);
        if (ret.getData() == null) {
            return ret;
        }
        ret.getData().setProductList(productDao.getProductsByGoodsId(id));
        List<Product> temp = ret.getData().getProductList();
        List<ProductVo> voList = new ArrayList<>();
        for (Product product : temp) {
            ProductVo productVo = (ProductVo) cloneVo(product, ProductVo.class);
            voList.add(productVo);
        }
        GoodsRetVo goodsRetVo = (GoodsRetVo) cloneVo(ret.getData(), GoodsRetVo.class);
        goodsRetVo.setProducts(voList);
        return new ReturnObject(goodsRetVo);
    }
}
