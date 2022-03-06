package cn.edu.xmu.oomall.aftersale.microservice;


import cn.edu.xmu.oomall.aftersale.microservice.vo.CustomerSimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "customer-service")
public interface CustomerService {
    @GetMapping("internal/customers/{id}")
    InternalReturnObject<CustomerSimpleVo> getSimpleCustomer(@PathVariable("id") Long id);
}
