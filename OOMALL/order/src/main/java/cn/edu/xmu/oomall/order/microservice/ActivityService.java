package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.AdvanceSaleRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.GroupOnActivityVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/16
 */
@Component
@FeignClient(value = "activity-service")
public interface ActivityService {
    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id advanceSaleId
     * @return 是否存在
     */
    @GetMapping("/internal/advancesale/{id}/exist")
    InternalReturnObject<Boolean> isAdvanceSaleExist(@PathVariable Long id);

    /**
     * 根据给定id判断数据库中是否有数据存在
     * @param id grouponId
     * @return 是否存在
     */
    @GetMapping("/internal/groupon/{id}/exist")
    InternalReturnObject<Boolean> isGrouponExist(@PathVariable Long id);

    /**
     * 查询上线预售活动的详细信息
     * @param id id
     * @return 预售活动详细信息
     */
    @GetMapping("/advancesales/{id}")
    InternalReturnObject<AdvanceSaleRetVo> queryOnlineAdvanceSaleInfo(@PathVariable Long id);

    /**
     * 查上线态团购活动详情(
     * @param id id
     * @return 团购活动详细信息
     */
    @GetMapping(value = "/groupons/{id}")
    InternalReturnObject<GroupOnActivityVo> getOnlineGroupOnActivity(@PathVariable Long id);
}
