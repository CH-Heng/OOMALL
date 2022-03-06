package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.customer.microservice.Vo.CouponActivityVo;
import cn.edu.xmu.oomall.customer.microservice.Vo.SimpleProductVo;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShoppingCartRetVo {
    private SimpleProductVo product;

    private Long quantity;

    private Long price;

    private List<CouponActivityVo> couponActivity;

}
