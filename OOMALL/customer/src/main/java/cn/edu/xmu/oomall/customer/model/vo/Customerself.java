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
public class Customerself implements VoObject{
    private Long id;
    private String userName;
    private String name;
    private String mobile;
    private String email;
    private Byte state;
    private Long point;

    public Customerself(Customer customer){
        this.id=customer.getId();
        this.userName= customer.getUserName();
        this.name=customer.getRealName();
        this.mobile= customer.getMobile();
        this.email= customer.getEmail();
        this.state=customer.getState();
        this.point=customer.getPoint();
    }

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
