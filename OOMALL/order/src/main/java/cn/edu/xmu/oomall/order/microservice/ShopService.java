package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.SimpleObjectRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Component
@FeignClient(value = "shop-service")
public interface ShopService {
    /**
     * 通过店铺id获得店铺姓名
     * @param id shopId
     * @return 查询结果
     */
    @GetMapping(value = "/shops/{id}")
    InternalReturnObject<SimpleObjectRetVo> getShopById(@PathVariable Long id);

}
