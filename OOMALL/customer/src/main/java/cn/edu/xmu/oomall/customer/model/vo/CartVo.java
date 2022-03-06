package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.microservice.Vo.SimpleProductVo;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.ShoppingCartPo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartVo implements VoObject{
    public Long id;
    public SimpleProductVo product;
    public Long quantity;
    public Long price;
    public CouponActivity couponActivity;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
