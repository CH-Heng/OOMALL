package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.core.util.ImgHelper;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.OnSaleDao;
import cn.edu.xmu.oomall.goods.dao.OnSaleGetDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.microservice.ActivityService;
import cn.edu.xmu.oomall.goods.microservice.FreightService;
import cn.edu.xmu.oomall.goods.microservice.ShopService;
import cn.edu.xmu.oomall.goods.microservice.vo.*;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.bo.OnSaleGetBo;
import cn.edu.xmu.oomall.goods.model.bo.Product;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.oomall.goods.model.po.ProductDraftPo;
import cn.edu.xmu.oomall.goods.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private ShopService shopService;

    @Autowired
    private FreightService freightService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OnSaleGetDao onSaleGetDao;

    @Value("${productservice.webdav.username}")
    private String davUsername;

    @Value("${productservice.webdav.password}")
    private String davPassWord;

    @Value("${productservice.webdav.baseUrl}")
    private String baseUrl;

    @Transactional(readOnly = true,rollbackFor=Exception.class)
    public ReturnObject listProductsByFreightId(Long shopId,Long fid,Integer pageNumber, Integer pageSize)
    {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        return productDao.listProductsByFreightId(fid,pageNumber,pageSize) ;
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject publishProduct(Long shopId,Long productId)
    {
        if(shopId!=0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject<Product> ret=productDao.publishById(productId);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Product product= ret.getData();
        ProductVo productVo=cloneVo(product,ProductVo.class);
        return new ReturnObject(productVo);
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject onshelvesProduct(Long shopId,Long productId)
    {
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.ONSHELF.getCode(),(byte)Product.ProductState.OFFSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK);
        }else{
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject offshelvesProduct(Long shopId,Long productId) {
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        if(!product.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该货品不属于该商铺");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject allowProduct(Long shopId,Long productId) {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        Product product = productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret = productDao.alterProductStates(product, (byte) Product.ProductState.OFFSHELF.getCode(), (byte) Product.ProductState.BANNED.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject prohibitProduct(Long shopId,Long productId)
    {
        if(shopId!=0){
            return new ReturnObject<Product>(ReturnNo.RESOURCE_ID_OUTSCOPE,"此商铺没有发布货品的权限");
        }
        Product product= productDao.getProduct(productId);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
        }
        ReturnObject ret=productDao.alterProductStates(product,(byte)Product.ProductState.BANNED.getCode(),(byte)Product.ProductState.OFFSHELF.getCode(),(byte)Product.ProductState.ONSHELF.getCode());
        if(ret.getData()!=null){
            return new ReturnObject(ReturnNo.OK,"成功");
        }else
        {
            return ret;
        }
    }
    @Transactional(readOnly = true)
    public ReturnObject getProductsOfCategories(Long did, Long cid, Integer page, Integer pageSize) {
        InternalReturnObject<SimpleCategoryVo> categoryReturnObj = shopService.getCategoryById(cid);
        Integer errno = categoryReturnObj.getErrno();
        if (errno != 0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = categoryReturnObj.getData();
        Long voId = categoryVo.getId();
        return Objects.isNull(voId)?new ReturnObject<>(ReturnNo.OK):
                new ReturnObject<>(productDao.getProductsOfCategories(did, cid,page,pageSize));
    }

    /**
     * 获取商品的所有状态
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    public ReturnObject getProductStates(){
        List<Map<String, Object>> stateList=new ArrayList<>();
        for (Product.ProductState enum1 : Product.ProductState.values()) {
            Map<String, Object> temp=new TreeMap<>();
            temp.put("code",enum1.getCode());
            temp.put("name",enum1.getState());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);

    }

    /**
     * 查询商品
     *
     * @param shopId,barCode,page,pageSize
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/11
     */
    @Transactional(readOnly=true)
    public ReturnObject getAllProducts(Long shopId, String barCode, Integer page, Integer pageSize) {
        return productDao.getAllProducts(shopId, barCode, page, pageSize);
    }

    /**
     * 获取product详细信息(非后台用户调用）
     *
     * @param productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject getProductDetails(Long productId) {
        ReturnObject ret =  productDao.getProductInfo(productId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product product = (Product) ret.getData();
        if(product.getState()!=Product.ProductState.ONSHELF.getCode()){
            product.setOnsaleId(null);
        }
        else {
            OnSalePo onSalePo = productDao.getValidOnSale(productId);
            if (onSalePo == null) {
                product.setOnsaleId(null);
            } else if (onSalePo.getState() != OnSaleGetBo.State.OFFLINE.getCode().byteValue()) {
                product.setOnsaleId(onSalePo.getId());
                product.setPrice(onSalePo.getPrice());
                product.setQuantity(onSalePo.getQuantity());
            }
        }

        //查找categoryName
        InternalReturnObject object = shopService.getCategoryById(product.getCategoryId());
        if (!object.getErrno().equals(0)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = (SimpleCategoryVo) object.getData();
        product.setCategoryName(categoryVo.getName());

        ProductRetVo vo = cloneVo(product, ProductRetVo.class);
        //检查是否被分享
        ReturnObject returnObject = onSaleGetDao.selectAnyOnsale(null,productId,null,null,1,10);
        if (returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        HashMap<String , Object> map =  (HashMap<String, Object>) returnObject.getData();
        ArrayList<SimpleOnSaleRetVo> simpleOnSaleRetVos = (ArrayList<SimpleOnSaleRetVo>) map.get("list");
        vo.setShareable(false);
        if (!simpleOnSaleRetVos.isEmpty()){
            for (SimpleOnSaleRetVo simpleOnSaleRetVo : simpleOnSaleRetVos){
                if (simpleOnSaleRetVo.getShareActId()!=null){
                    InternalReturnObject<ShareActivitySimpleRetVo> i = activityService.getShareActivityById(simpleOnSaleRetVo.getShareActId());
                    ShareActivitySimpleRetVo shareActivityRetVo = i.getData();
                    if (shareActivityRetVo.getState()==(byte)1){
                        vo.setShareable(true);
                    }
                }
            }
        }
        return new ReturnObject(vo);
    }

    @Transactional(readOnly=true)
    public ReturnObject isProductExit(Long productId) {
        ReturnObject ret =  productDao.getProductInfo(productId);
        if(ret.getCode()!=ReturnNo.OK||ret.getData()==null){
            return new ReturnObject<>(false);
        }else{
            return new ReturnObject<>(true);
        }
    }


    /**
     * 获取某一商铺的product详细信息（后台用户调用）
     *
     * @param shopId,productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(readOnly=true)
    public ReturnObject getShopProductDetails(Long shopId, Long productId, Long loginUser , String loginUsername) {
        ReturnObject check = productDao.matchProductShop(productId, shopId);
        if (check.getCode() != ReturnNo.OK) {
            return new ReturnObject<>(check.getCode());
        }
        ReturnObject ret =  productDao.getProductInfo(productId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product product = (Product) ret.getData();
        OnSalePo onSalePo = productDao.getValidOnSale(productId);
        if(onSalePo==null){
            product.setOnsaleId(null);
        }
        else {
            product.setOnsaleId(onSalePo.getId());
        }

        InternalReturnObject object = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = (SimpleCategoryVo) object.getData();
        product.setCategoryName(categoryVo.getName());

        return new ReturnObject(cloneVo(product, ProductShopRetVo.class));
    }

    @Transactional(readOnly = true)
    public ReturnObject getShopDraftProduct(Long shopId,Integer page,Integer pageSize){
        return productDao.getShopDraftProduct(shopId,page,pageSize);
    }

    /**
     * 获取某一商铺的draftProduct详细信息（后台用户调用）
     *
     * @param shopId,productId
     * @return ReturnObject
     * @author wyg
     * @Date 2021/12/23
     */
    @Transactional(readOnly=true)
    public ReturnObject getShopDraftProductDetails(Long shopId, Long id, Long loginUser , String loginUsername) {
        ProductDraftPo po = productDao.getProductDraft(id);
        if (po == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!shopId.equals(po.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }

        Product product = cloneVo(po,Product.class);
        InternalReturnObject object = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = (SimpleCategoryVo) object.getData();
        product.setCategoryName(categoryVo.getName());

        DraftProductShopRetVo p = cloneVo(product, DraftProductShopRetVo.class);
        p.setProductId(po.getProductId());
        return new ReturnObject(p);
    }

    /**
     * 将product添加到good中
     *
     * @param
     * @return ReturnObject
     * @author wyg
     * @date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject addProductToGood(Long shopId, ProductDetailVo productVo, Long loginUser, String loginUsername) {
        ProductDraftPo po = cloneVo(productVo, ProductDraftPo.class);
        po.setShopId(shopId);
        setPoCreatedFields(po,loginUser,loginUsername);

        ReturnObject ret = productDao.newProduct(po);

        Product product = (Product) ret.getData();

        //查找shopName
        InternalReturnObject object = shopService.getShopInfo(product.getShopId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleShopVo simpleShopVo =  cloneVo(object.getData(),SimpleShopVo.class);
        product.setShopName(simpleShopVo.getName());
        //查找categoryName
        InternalReturnObject object1 = shopService.getCategoryById(product.getCategoryId());
        if(!object.getErrno().equals(0)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleCategoryVo categoryVo = cloneVo(object.getData(),SimpleCategoryVo.class);
        product.setCategoryName(categoryVo.getName());

        ProductNewReturnVo vo = cloneVo(product, ProductNewReturnVo.class);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        return new ReturnObject(vo);
    }

    /**
     * 上传货品图片
     *
     * @param shopId, id, multipartFile
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject upLoadProductImg(Long shopId, Long id, MultipartFile multipartFile, Long loginUser, String loginUsername) {
        ProductDraftPo p  = productDao.getProductDraft(id);
        if(p == null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(shopId!=0&&!shopId.equals(p.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject;

        try {
            returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassWord, baseUrl);
            //文件上传错误
            if (returnObject.getCode() != ReturnNo.OK) {
                return returnObject;
            }

            //更新数据库
            String oldFilename = p.getImageUrl();
            p.setImageUrl(returnObject.getData().toString());
            setPoModifiedFields(p,loginUser,loginUsername);

            ReturnObject updateReturnObject = productDao.updateDraftProductById(p);

            //数据库更新失败，需删除新增的图片
            if (updateReturnObject.getCode() == ReturnNo.FIELD_NOTVALID || updateReturnObject.getCode() == ReturnNo.INTERNAL_SERVER_ERR) {
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功，删除原来的图片
            if (updateReturnObject.getCode() == ReturnNo.OK) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassWord, baseUrl);
                return updateReturnObject;
            }

        } catch (Exception e) {
            return new ReturnObject(ReturnNo.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }

    /**
     * 物理删除Products
     *
     * @param shopId, id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject deleteDraftProductById(Long shopId, Long id, Long loginUser, String loginUsername) {
        if(shopId!=0){
            ProductDraftPo productDraftPo = productDao.getProductDraft(id);
            if(productDraftPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(shopId.longValue()!=productDraftPo.getShopId()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        }
        ReturnObject ret = productDao.deleteDraftProductById(id);
        if(ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        return ret;
    }

    /**
     * 更新draftProducts
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject updateDraftProduct(Long shopId, Long id, ProductChangeVo productChangeVo,Long loginUser, String loginUsername) {
        ProductDraftPo productDraftPo = productDao.getProductDraft(id);
        if (productDraftPo == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!shopId.equals(0L) &&!shopId.equals(productDraftPo.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ProductDraftPo po = cloneVo(productChangeVo, ProductDraftPo.class);
        po.setId(id);
        setPoModifiedFields(po, loginUser, loginUsername);
        ReturnObject ret = productDao.updateDraftProductById(po);
        return ret;
    }

    /**
     * 修改线下态货品
     *
     * @param shopId, id, productChangeVo
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/12
     */
    @Transactional(rollbackFor= Exception.class)
    public ReturnObject changeProduct(Long shopId, Long id, ProductChangeVo p, Long loginUser, String loginUsername) {
        Product product = productDao.getProduct(id);
        if (product.getState()==(byte)-1) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if (shopId != 0 && !shopId.equals(product.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }

        setPoCreatedFields(product,loginUser,loginUsername);
        if(p.getSkuSn()!=null){
            product.setSkuSn(p.getSkuSn());
        }
        if(p.getName()!=null){
            product.setName(p.getName());
        }
        if(p.getCategoryId()!=null){
            product.setCategoryId(p.getCategoryId());
        }
        if(p.getBarCode()!=null){
            product.setBarcode(p.getBarCode());
        }
        if(p.getOriginalPrice()!=null){
            product.setOriginalPrice(p.getOriginalPrice());
        }
        if(p.getOriginPlace()!=null){
            product.setOriginPlace(p.getOriginPlace());
        }
        if(p.getUnit()!=null){
            product.setUnit(p.getUnit());
        }
        if(p.getWeight()!=null){
            product.setWeight(p.getWeight());
        }
        ReturnObject ret = productDao.addDraftProduct(product,loginUser,loginUsername);
        return ret;
    }

    /**
     * 获取Good集合中的Product
     *
     * @param id
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/10
     */
    @Transactional(readOnly=true)
    public ReturnObject getGoodsProductById(Long id) {
        ReturnObject ret = productDao.getGoodsProductById(id);
        if(ret.getCode() != ReturnNo.OK){
            return ret;
        }
        GoodsSimpleRetVo vo = (GoodsSimpleRetVo)ret.getData();
        return new ReturnObject(vo);
    }

    /**
     * 将上线态的秒杀商品加载到Redis
     * @param beginTime, endTime
     * @return ReturnObject
     * @author wyg
     * @Date 2021/11/16
     */
    @Transactional(readOnly = true)
    public ReturnObject loadSecondKillProduct(LocalDateTime beginTime, LocalDateTime endTime) {
        return new ReturnObject(productDao.loadSecondKillProduct(beginTime,endTime));
    }

    @Transactional(readOnly = true)
    public ReturnObject getFreightModels(Long shopId, Long id, Long loginUser, String loginUsername) {
        ReturnObject ret = productDao.matchProductShop(id,shopId);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Product p = productDao.getProduct(id);
        if (p.getFreightId() != null) {
            return new ReturnObject(freightService.getFreightModel(shopId,p.getFreightId()).getData());
        } else {
            return new ReturnObject(freightService.getDefaultFreightModel(shopId).getData());
        }
    }

    @Transactional(rollbackFor= Exception.class)
    public ReturnObject changeFreightModels(Long id,Long fid, Long loginUser, String loginUsername) {
        Product po = productDao.getProduct(id);
        if (po == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        InternalReturnObject<Boolean> objFreight=freightService.existFreightModel(fid);
        if(objFreight.getData().equals(false)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Product p = new Product();
        p.setId(id);
        p.setFreightId(fid);
        ReturnObject ret = productDao.changeProduct(p,loginUser,loginUsername);
        return ret;
    }

    @Transactional(readOnly = true,rollbackFor= Exception.class)
    public ReturnObject existProduct(Long id){
        ReturnObject obj=productDao.getProductById(id);
        if(!obj.getCode().equals(ReturnNo.OK)){
            return new ReturnObject(false);
        }else{
            if(obj.getData()==null){
                return new ReturnObject(false);
            }
            return new ReturnObject(true);
        }
    }

    @Transactional(rollbackFor= Exception.class)
    public InternalReturnObject getProduct(Long id) {
        Product product = productDao.getProduct(id);
        if (product.getState() == (byte) -1) {
            return new InternalReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST.getCode(), "操作的资源不存在");
        }
        InternalProductRetVo productRetVo = cloneVo(product, InternalProductRetVo.class);
        OnSalePo onSalePo = productDao.getValidOnSale(id);
        if (onSalePo == null) {
            product.setOnsaleId(null);
        } else {
            product.setOnsaleId(onSalePo.getId());
        }
        productRetVo.setPrice(onSalePo.getPrice());
        InternalReturnObject<CategoryCommissionVo> ret = shopService.getCategoryDetail(product.getCategoryId());
        if (ret.getData() == null) {
            return ret;
        }
        productRetVo.setCategory(ret.getData());
        return new InternalReturnObject(productRetVo);
    }
}
