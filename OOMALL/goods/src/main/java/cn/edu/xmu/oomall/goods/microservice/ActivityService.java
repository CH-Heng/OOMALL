package cn.edu.xmu.oomall.goods.microservice;

import cn.edu.xmu.oomall.goods.microservice.vo.ShareActivityRetVo;
import cn.edu.xmu.oomall.goods.microservice.vo.ShareActivitySimpleRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "activity-service")
public interface ActivityService {
    @GetMapping( "internal/shareactivities/{id}")
    InternalReturnObject<ShareActivitySimpleRetVo> getShareActivityById(@PathVariable Long id);
}
