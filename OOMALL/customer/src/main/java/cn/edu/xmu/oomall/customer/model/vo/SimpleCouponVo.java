package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.model.bo.Coupon;
import cn.edu.xmu.oomall.customer.model.po.CouponPo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleCouponVo implements VoObject{
    private Long id;
    private Long activityId;
    private String name;
    private String couponSn;
    private Integer state;
    private String beginTime;
    private String endTime;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
