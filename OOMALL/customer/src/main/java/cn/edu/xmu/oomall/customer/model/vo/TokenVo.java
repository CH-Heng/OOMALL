package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.po.CustomerPo;
import lombok.*;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.apache.ibatis.ognl.Token;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenVo implements VoObject{
    private Long userId;
    private String userName;

    public TokenVo(Customer customer){
        this.userId=customer.getId();
        this.userName=customer.getUserName();
    }

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
