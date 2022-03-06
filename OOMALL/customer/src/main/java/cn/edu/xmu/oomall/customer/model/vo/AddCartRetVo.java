package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.ShoppingCartPo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddCartRetVo implements VoObject{
    private Long id;
    private Long quantity;
    private Long price;

    public Object AddCartRetVo(Long id,Long quantity,Long price){
        this.id=id;
        this.quantity=quantity;
        this.price=price;
        return this;
    }

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
