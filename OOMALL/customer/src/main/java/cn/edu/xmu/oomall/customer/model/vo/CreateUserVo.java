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
public class CreateUserVo implements VoObject{
    private String mobile;
    private String email;
    private String userName;
    private String password;
    private String name;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
