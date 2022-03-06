package cn.edu.xmu.oomall.customer.microservice.Vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleProductVo implements VoObject{
    private Long id;
    private String name;
    private String imageUrl;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
