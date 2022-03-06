package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.FreightCalculatingPostVo;
import cn.edu.xmu.oomall.order.microservice.vo.FreightCalculatingRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Component
@FeignClient(value = "freight-service")
public interface FreightService {
    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id regionId
     * @return 是否存在
     */
    @GetMapping("/internal/region/{id}/exist")
    InternalReturnObject<Boolean> isRegionExist(@PathVariable Long id);


    /**
     * 计算运费
     * @param rid 地区id
     * @param items 各种参数
     * @return 计算结果
     */
    @PostMapping("/regions/{rid}/price")
    InternalReturnObject<FreightCalculatingRetVo> calculateFreight(@PathVariable Long rid, @RequestBody List<FreightCalculatingPostVo> items);

}
