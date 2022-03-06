package cn.edu.xmu.oomall.customer.model.vo;

import lombok.*;
import cn.edu.xmu.oomall.core.model.VoObject;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponStateVo implements VoObject{
    private Integer code;
    private String name;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
