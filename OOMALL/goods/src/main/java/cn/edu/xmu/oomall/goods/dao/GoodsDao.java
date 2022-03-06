package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.po.GoodsPo;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Repository
public class GoodsDao {
    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);

    @Autowired
    private GoodsPoMapper goodsPoMapper;

    @Autowired
    private ProductPoMapper productPoMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Value("${oomall.goods.onsale.expiretime}")
    private Long goodsTimeout;

    public final static String GOODSKEY="goods_%d";

    public ReturnObject createNewGoods(Goods goods)
    {
        try
        {
            GoodsPo goodsPo=(GoodsPo) cloneVo(goods,GoodsPo.class);
            goodsPoMapper.insert(goodsPo);
            return new ReturnObject((Goods) cloneVo(goodsPo,Goods.class));
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateGoods(Goods goods)
    {
        try
        {
            GoodsPo oldGoods=goodsPoMapper.selectByPrimaryKey(goods.getId());
            if(oldGoods==null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"商品id不存在");
            }
            if(!oldGoods.getShopId().equals(goods.getShopId())) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE, "该商品不属于该商铺");
            }
            oldGoods.setName(goods.getName());
            setPoModifiedFields(oldGoods,goods.getModifierId(),goods.getModifierName());
            goodsPoMapper.updateByPrimaryKeySelective(oldGoods);
            return new ReturnObject<>(ReturnNo.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject<Goods> searchGoodsById(Long shopId,Long id)
    {
        try {
            Goods goods = new Goods();
            GoodsPo goodsPo = goodsPoMapper.selectByPrimaryKey(id);
            if (goodsPo == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"商品id不存在");
            }
            if (!goodsPo.getShopId().equals(shopId)) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "该商品不属于该商铺");
            }
            goods = (Goods) cloneVo(goodsPo, Goods.class);
            return new ReturnObject<>(goods);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
    public ReturnObject<Object> deleteGoodsById(Long shopId,Long id) {
        GoodsPo goodsPo;
        try {
            String key=String.format(GOODSKEY,id);
            redisUtils.del(key);
            goodsPo = goodsPoMapper.selectByPrimaryKey(id);
            if (goodsPo == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"商品id不存在");
            }
            if (!goodsPo.getShopId().equals(shopId)) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该商品不属于该商铺");
            }
            goodsPoMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return new ReturnObject(ReturnNo.OK);
    }
}
