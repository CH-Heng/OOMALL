package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk
 * @date 2021/11/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRetVo {

    private Long id;

    private Long orderId;

    private Long shopId;

    private Long productId;

    private Long onsaleId;

    private Integer quantity;

    private Long price;

    private Long discountPrice;

    private Long point;

    private String name;

    private Long customerId;
}
