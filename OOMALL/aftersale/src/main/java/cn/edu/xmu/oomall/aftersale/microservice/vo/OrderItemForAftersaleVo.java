package cn.edu.xmu.oomall.aftersale.microservice.vo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemForAftersaleVo {
    private Long id;
    private Long shopId;
    private Long customerId;
    private Integer state;
    private Long productId;
    private Long orderId;
    private Long onsaleId;
    private String name;
    private Integer quantity;
    private Long price;
    private Long discountPrice;
    private Long point;
}
