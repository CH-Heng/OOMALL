package cn.edu.xmu.oomall.payment.microservice;

import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/18/19:53
 */

@Component
@FeignClient(value = "order-service")
public interface OrderService {
    /**
     * 根据orderSn对父订单进行分单
     * @param orderSn
     * @return
     */
    @Audit
    @PostMapping("internal/orderperate")
    public Object seperateOrdersByOrderSn(@RequestParam String orderSn);

}
