package cn.edu.xmu.oomall.aftersale.model.vo;

import cn.edu.xmu.oomall.aftersale.microservice.vo.CustomerSimpleVo;
import cn.edu.xmu.oomall.aftersale.microservice.vo.SimpleRegionVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AftersaleRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private CustomerSimpleVo customer;
    private String shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private Long price;
    private Long quantity;
    private SimpleRegionVo region;
    private String details;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;

}
