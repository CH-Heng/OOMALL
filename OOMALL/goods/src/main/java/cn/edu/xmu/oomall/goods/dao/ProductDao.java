package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Goods;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.*;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.goods.mapper.ProductPoMapper;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * @author yujie lin
 * @date 2021/11/11
 *
 * @author 黄添悦
 * @date 2021/11/25
 *
 * @author 王文飞
 * @date 2021/11/25
 *
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Repository
public class ProductDao {
    private Logger logger = LoggerFactory.getLogger(OnSaleDao.class);
    private final static String PRODUCT_ID="p_%d";

    @Autowired
    private ProductPoMapper productMapper;
    @Autowired
    private OnSalePoMapper onSaleMapper;
    @Autowired
    private GoodsPoMapper goodsPoMapper;
    @Autowired
    private ProductDraftPoMapper productDraftPoMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.goods.product.expiretime}")
    private Long productTimeout;

    private final static String GOODSKEY="goods_%d";

    public ReturnObject hasExist(Long productId) {
        try{
            ProductPo po= productMapper.selectByPrimaryKey(productId);
            return new ReturnObject(null != po);
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject matchProductShop(Long productId , Long shopId) {
        try{
            ProductPo productPo=productMapper.selectByPrimaryKey(productId);
            if(productPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(shopId.equals(productPo.getShopId()));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 该方法用于频繁查询onsale详情的api中，故使用redis
     * @author Zijun Min 22920192204257
     * @param id
     * @return 返回的是Product类型
     */
    public ReturnObject getProductInfo(Long id){
        try{
            String key = String.format(PRODUCT_ID,id);
            Product product=(Product) redisUtil.get(key);
            if(null!=product){
                return new ReturnObject(product);
            }else {
                ProductPo productPo = productMapper.selectByPrimaryKey(id);
                if (productPo == null) {
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                } else {
                    Product pro=cloneVo(productPo,Product.class);
                    redisUtil.set(key,pro,productTimeout);
                    return new ReturnObject(pro);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
    /**
     * @author 黄添悦
     * @author 王文飞
     * @date 2021/11/25
     */
    public ReturnObject listProductsByFreightId(Long fid, Integer pageNumber, Integer pageSize)
    {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            ProductPoExample productPoExample = new ProductPoExample();
            ProductPoExample.Criteria cr = productPoExample.createCriteria();
            cr.andFreightIdEqualTo(fid);
            List<ProductPo> products = productMapper.selectByExample(productPoExample);
            if(products.size()==0)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret=new ReturnObject(new PageInfo<ProductPo>(products));
            return Common.getPageRetVo(ret,ProductVo.class);
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

    }
    public ReturnObject alterProductStates(Product product,Byte targetState,Byte... states){
        try{
            ProductPo productPo=new ProductPo();
            productPo.setId(product.getId());
            productPo.setState(product.getState());
            boolean ifValid=false;
            for(Byte state:states){
                if(productPo.getState().equals(state)) {
                    ifValid=true;
                }
            }if(ifValid){
                productPo.setState(targetState);
                productMapper.updateByPrimaryKeySelective(productPo);
                String key = String.format(PRODUCT_ID, productPo.getId());
                redisUtil.del(key);
                return new ReturnObject(productPo);
            }else{
                return new ReturnObject(ReturnNo.STATENOTALLOW,"当前货品状态不支持进行该操作");
            }
        }catch(Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * @author 黄添悦
     * @author 王文飞
     * @date 2021/11/25
     */
    public ReturnObject publishById(Long id) {
        try {
            ProductDraftPo productDraftPo = productDraftPoMapper.selectByPrimaryKey(id);
            if(productDraftPo==null)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ProductPo productPo = (ProductPo) cloneVo(productDraftPo, ProductPo.class);
            if (productDraftPo.getProductId() == 0) {
                productPo.setId(null);
                productPo.setState((byte)Product.ProductState.OFFSHELF.getCode());
                productMapper.insert(productPo);
            } else {
                productPo.setId(productDraftPo.getProductId());
                productPo.setState((byte)Product.ProductState.OFFSHELF.getCode());
                productMapper.updateByPrimaryKey(productPo);
            }
            String key = String.format(GOODSKEY, productPo.getGoodsId());
            String key2 = String.format(PRODUCT_ID,productPo.getId());
            redisUtil.del(key);
            redisUtil.del(key2);
            productDraftPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject((Product) cloneVo(productPo, Product.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public List<Product> getProductsByGoodsId(Long id){
        ProductPoExample productPoExample = new ProductPoExample();
        ProductPoExample.Criteria cr = productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        List<ProductPo> products = productMapper.selectByExample(productPoExample);
        List<Product> productList = new ArrayList<>(products.size());
        for (ProductPo productPo : products) {
            productList.add((Product) cloneVo(productPo, Product.class));
        }
        return productList;
    }

    public int resetGoodsIdForProducts(Long id,Long newId){
        ProductPoExample productPoExample = new ProductPoExample();
        ProductPoExample.Criteria cr = productPoExample.createCriteria();
        cr.andGoodsIdEqualTo(id);
        ProductPo productPo=new ProductPo();
        productPo.setGoodsId(newId);
        return productMapper.updateByExampleSelective(productPo,productPoExample);
    }
    public Product getProduct(Long id) {
        ProductPo productPo = productMapper.selectByPrimaryKey(id);
        if (productPo != null) {
            return cloneVo(productPo, Product.class);
        }
        Product product=new Product();
        product.setState((byte)-1);
        return product;
    }
    public ProductDraftPo getProductDraft(Long id){
        try{
            return productDraftPoMapper.selectByPrimaryKey(id);
        }catch (Exception e){
            return null;
        }
    }

    public Object getProductsOfCategories(Long did, Long cid, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        ProductPoExample example = new ProductPoExample();
        ProductPoExample.Criteria criteria=example.createCriteria()
                .andCategoryIdEqualTo(cid);
        if (Objects.nonNull(did)){
            criteria.andShopIdEqualTo(did);
        }else{
            criteria.andStateEqualTo((byte)(Product.ProductState.ONSHELF.getCode()));
        }
        List<ProductPo> productPos;
        try {
            productPos = productMapper.selectByExample(example);
        } catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return new PageInfo<>(productPos);
    }

    /**
     * 用barCode和shopId查找product
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    public ReturnObject getAllProducts(Long shopId, String barCode, Integer page, Integer pageSize) {
        ProductPoExample example = new ProductPoExample();
        ProductPoExample.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(barCode)) {
            criteria.andBarcodeEqualTo(barCode);
        }

        List<ProductPo> productPoList = new ArrayList<>();
        List<SimpleProductRetVo> productSimpleRetVos = new ArrayList<>();
        try {
            if (example.getOredCriteria().size() != 0) {
                productPoList = productMapper.selectByExample(example);
            }
            for (ProductPo po : productPoList) {
                Product product = cloneVo(po, Product.class);
                productSimpleRetVos.add( cloneVo(product, SimpleProductRetVo.class));
            }

            PageInfo<SimpleProductRetVo> pageInfo = PageInfo.of(productSimpleRetVos);
            return Common.getPageRetVo(new ReturnObject(pageInfo), SimpleProductRetVo.class);
        } catch (Exception e) {
            logger.error("selectAllProducts: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 用productId获取当前有效的OnSale
     * @author wyg
     * @Date 2021/11/22
     */
    public OnSalePo getValidOnSale(Long id){
        OnSalePoExample example = new OnSalePoExample();
        OnSalePoExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(id);
        criteria.andStateEqualTo(OnSale.State.ONLINE.getCode().byteValue());

        List<OnSalePo> onSalePos = onSaleMapper.selectByExample(example);
        if (onSalePos.size() != 1) {
            return null;
        }
        else{
            return onSalePos.get(0);
        }
    }

    /**
     * 新建Product
     * @return ReturnObject
     * @author wyg
     * @date 2021/11/12
     */
    public ReturnObject newProduct(ProductDraftPo po){
        try{
            int ret;
            ret = productDraftPoMapper.insertSelective(po);
            if(ret==0){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            Product product = (Product) cloneVo(po, Product.class);
            return new ReturnObject(product);
        }catch (Exception e){
            logger.error("newProduct: DataAccessException:" + e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 更新审核态的product
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    public ReturnObject addDraftProduct(Product product, Long loginUser, String loginUsername) {
        //复制前置空productId,因为ProductDraftPo主键自增
        Long temp = product.getId();
        product.setId(null);
        ProductDraftPo productDraftPo =  cloneVo(product, ProductDraftPo.class);
        productDraftPo.setProductId(temp);
        int ret;
        try{
            //插入更新信息
            setPoCreatedFields(productDraftPo,loginUser,loginUsername);
            ret = productDraftPoMapper.insertSelective(productDraftPo);
        }catch (Exception e){
            logger.error("updateProduct: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if (ret == 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {
            return new ReturnObject();
        }
    }

    /**
     * 更新审核态Product
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    public ReturnObject updateDraftProductById(ProductDraftPo p) {
        try {
            int ret;
            ret =  productDraftPoMapper.updateByPrimaryKeySelective(p);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject();
            }
        }catch (Exception e){
            logger.error("deleteProduct: DataAccessException:" + e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 物理删除审核态Product
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    public ReturnObject deleteDraftProductById(Long id) {
        try {
            int ret;
            ret =  productDraftPoMapper.deleteByPrimaryKey(id);

            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject();
            }
        }catch (Exception e){
            logger.error("deleteProduct: DataAccessException:" + e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 获取goods和其中的商品
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    public ReturnObject getGoodsProductById(Long goodsId) {
        try {
            GoodsPo goodsPo=goodsPoMapper.selectByPrimaryKey(goodsId);
            if(goodsPo == null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            Goods goods = cloneVo(goodsPo,Goods.class);
            GoodsSimpleRetVo goodsSimpleRetVo = cloneVo(goods, GoodsSimpleRetVo.class);

            List<Product> products = getProductsByGoodsId(goodsId);
            List<SimpleProductRetVo> productSimpleRetVos = new ArrayList<>();
            for (Product product : products) {
                productSimpleRetVos.add( cloneVo(product,SimpleProductRetVo.class));
            }

            goodsSimpleRetVo.setProductList(productSimpleRetVos);
            return new ReturnObject(goodsSimpleRetVo);
        }catch (Exception e){
            logger.error("selectGoods: DataAccessException:" + e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }


    public ReturnObject getShopDraftProduct(Long shopId,Integer page,Integer pageSize){
        try {
            PageHelper.startPage(page, pageSize, true, true, true);
            ProductDraftPoExample productPoExample = new ProductDraftPoExample();
            ProductDraftPoExample.Criteria cr = productPoExample.createCriteria();
            if(shopId!=0){
                cr.andShopIdEqualTo(shopId);
            }
            List<ProductDraftPo> products = productDraftPoMapper.selectByExample(productPoExample);
            ReturnObject<PageInfo<Object>> ret=new ReturnObject(new PageInfo<>(products));
            return Common.getPageRetVo(ret, SimpleDraftProductRetVo.class);
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 将上线态的秒杀商品加载到Redis
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/16
     */
    public ReturnObject loadSecondKillProduct(LocalDateTime beginTime, LocalDateTime endTime) {
        OnSalePoExample onSalePoExample = new OnSalePoExample();
        OnSalePoExample.Criteria criteria = onSalePoExample.createCriteria();
        criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        criteria.andEndTimeLessThanOrEqualTo(endTime);
        criteria.andTypeEqualTo(OnSale.Type.SECKILL.getCode().byteValue());
        criteria.andStateEqualTo(OnSale.State.ONLINE.getCode().byteValue());

        try {
            List<OnSalePo> onSalePos = onSaleMapper.selectByExample(onSalePoExample);

            for (OnSalePo onSalePo : onSalePos) {
                ProductPo productPo = productMapper.selectByPrimaryKey(onSalePo.getProductId());
                if(productPo == null){
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
                }
                Product product = cloneVo(productPo, Product.class);
                //获取当前时间到结束时间的秒数
                Duration duration = Duration.between(LocalDateTime.now(),endTime);
                Long seconds = duration.toSeconds();
                redisUtil.set(String.format(PRODUCT_ID, product.getId()) , product, seconds);

            }
            return new ReturnObject();
        } catch (Exception e) {
            logger.error("selectGoods: DataAccessException:" + e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * @author wwk's father
     * @date 2021/12/23
     */
    public ReturnObject changeProduct(Product product,Long loginUser,String loginUserName) {
        ProductPo po = cloneVo(product, ProductPo.class);
        setPoModifiedFields(po, loginUser, loginUserName);
        try {
            productMapper.updateByPrimaryKeySelective(po);
            String key = String.format(PRODUCT_ID, po.getId());
            redisUtil.del(key);
            return new ReturnObject();
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getProductById(Long id){
        try{
            ProductPo productPo=productMapper.selectByPrimaryKey(id);
            if(productPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }else{
                return new ReturnObject(productPo);
            }
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
