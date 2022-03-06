package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "用于换货的订单明细")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemForExchangeVo { //价格为0
    private Long productId;
    private String name;
    private Long quantity;
}
