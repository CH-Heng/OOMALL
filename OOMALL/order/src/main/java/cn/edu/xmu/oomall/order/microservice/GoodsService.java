package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.QuantityVo;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "goods-service")
public interface GoodsService {
    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id couponActId
     * @return 是否存在
     */
    @GetMapping("/internal/product/{id}/exist")
    InternalReturnObject<Boolean> isProductExist(@PathVariable Long id);

    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id onSaleId
     * @return 是否存在
     */
    @GetMapping("/internal/onsale/{id}/exist")
    InternalReturnObject<Boolean> isOnsaleExist(@PathVariable Long id);

    /**
     * 内部API- 查询特定价格浮动的详情，该方法加入redis
     * @param id
     * @return 所有类型都会返回
     */
    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnSaleRetVo> selectFullOnsale(@PathVariable("id")Long id);

    /**
     * 获得商品详情
     * @param id 商品详情
     * @return
     */
    @GetMapping("/products/{id}")
    InternalReturnObject<ProductRetVo> getProductDetails(@PathVariable Long id);

    /**
     * 减少库存
     * @param did
     * @param id
     * @param vo
     * @return
     */
    @Audit
    @PutMapping("internal/shops/{did}/onsales/{id}/decr")
    InternalReturnObject decreaseOnSale(@PathVariable Long did, @PathVariable Long id, @RequestBody QuantityVo vo);

    /**
     * 增加库存
     * @param did
     * @param id
     * @param vo
     * @return
     */
    @Audit
    @PutMapping("internal/shops/{did}/onsales/{id}/incr")
    InternalReturnObject increaseOnSale(@PathVariable Long did, @PathVariable Long id, @RequestBody QuantityVo vo);

}
