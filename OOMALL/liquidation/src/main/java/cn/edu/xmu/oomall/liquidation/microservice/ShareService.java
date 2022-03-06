package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wwk
 * @date 2021/12/11
 */
@Component
@FeignClient(name = "share-service")
public interface ShareService {

    /**
     * 内部API
     * 根据 onsaleId 和 customerId 获得分享
     * @return SuccessfulShareRetVo
     */
    @GetMapping("Internal/share")
    InternalReturnObject getSuccessfulShareByOnSaleIdAdnCustomerId(@RequestParam Long onsaleId,
                                                                   @RequestParam Long customerId);

    /**
     * 内部API
     * 将分享成功置为已清算状态
     */
    @PutMapping("Internal/beshared/{id}/liquidated")
    InternalReturnObject setSuccessfulShareLiquidated(@PathVariable Long id);
}
