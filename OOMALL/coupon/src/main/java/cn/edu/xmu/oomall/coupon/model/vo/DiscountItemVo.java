package cn.edu.xmu.oomall.coupon.model.vo;

import cn.edu.xmu.oomall.coupon.model.bo.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/10 14:52
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountItemVo {
    private Long productId;
    private Long onsaleId;
    private Long quantity;
    private Long originalPrice;
    private Long activityId;

    /**
     * 生成对应的orderitem，由于属性名不同，故不使用cloneVo
     */
    public OrderItem getOrderItem(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(this.productId);
        orderItem.setOnsaleId(this.onsaleId);
        orderItem.setQuantity(this.quantity);
        orderItem.setCouponActivityId(this.activityId);
        orderItem.setPrice(this.originalPrice);
        return orderItem;
    }
}
