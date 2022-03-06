package cn.edu.xmu.oomall.liquidation.microservice.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AftersaleRetVo {
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private SimpleObjectVo customer;
    private String shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private Long price;
    private Integer quantity;
    private SimpleObjectVo region;
    private String details;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;
}