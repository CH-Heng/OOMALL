package cn.edu.xmu.oomall.customer.microservice.Vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleCategory {
    private Long id;
    private String name;
}
