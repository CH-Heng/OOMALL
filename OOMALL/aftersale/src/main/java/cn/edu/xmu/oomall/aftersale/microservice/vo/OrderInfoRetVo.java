package cn.edu.xmu.oomall.aftersale.microservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 张晖婧
 */
@Data
@NoArgsConstructor
public class OrderInfoRetVo {
    private Long id;
    private String orderSn;
    private SimpleCustomerVo customer;
    private SimpleShopVo shop;
    private Long pid;
    private Byte state;
    private String confirmTime;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private String message;
    private Long regionId;
    private String address;
    private String mobile;
    private String consignee;
    private Long grouponId;
    private Long advancesaleId;
    private String shipmentSn;
    private List<SimpleOrderItemVo> orderItems;

}
