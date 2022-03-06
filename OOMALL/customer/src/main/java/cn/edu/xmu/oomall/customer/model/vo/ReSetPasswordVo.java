package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.model.bo.Address;
import cn.edu.xmu.oomall.customer.model.po.AddressPo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReSetPasswordVo implements VoObject{
    private String email;
    private String name;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
