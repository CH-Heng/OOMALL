package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.po.CustomerPo;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleCustomer implements VoObject{
    private Long id;
    private String name;

    public SimpleCustomer(Customer customer){
        this.id=customer.getId();
        this.name=customer.getRealName();
    }

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
