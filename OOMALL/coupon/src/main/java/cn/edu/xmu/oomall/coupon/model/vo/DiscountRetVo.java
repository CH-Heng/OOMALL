package cn.edu.xmu.oomall.coupon.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/10 14:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRetVo{
    private Long productId;
    private Long onsaleId;
    private Long discountPrice;
    private Long activityId;

}
