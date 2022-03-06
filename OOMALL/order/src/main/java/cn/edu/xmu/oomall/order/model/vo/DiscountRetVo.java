package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mzj
 * @date 2021/12/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRetVo{
    private Long productId;
    private Long onsaleId;
    private Long discountPrice;
    private Long activityId;

}