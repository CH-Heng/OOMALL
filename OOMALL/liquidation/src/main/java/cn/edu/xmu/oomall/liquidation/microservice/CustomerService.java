package cn.edu.xmu.oomall.liquidation.microservice;

import cn.edu.xmu.oomall.liquidation.microservice.vo.CustomerPointVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Component
@FeignClient(name = "customer-service")
public interface CustomerService {

    /**
     * 内部API
     * 买家返点变动
     * @return CustomerRetVo
     */
    @PutMapping("/internal/customers/{id}/point")
    InternalReturnObject modifyCustomerPoint(@PathVariable Long id,
                                             @RequestBody CustomerPointVo vo);
}
