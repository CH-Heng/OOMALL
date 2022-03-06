package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "用于换货的订单视图")
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoForExchangeVo {
    private OrderItemForExchangeVo orderItems;
    private Long customerId;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
}
