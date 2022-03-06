package cn.edu.xmu.oomall.aftersale.microservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleOrderItemVo {
    private Long id;
    private Long productId;
    private String name;
    private Long quantity;
    private Long price;
}
