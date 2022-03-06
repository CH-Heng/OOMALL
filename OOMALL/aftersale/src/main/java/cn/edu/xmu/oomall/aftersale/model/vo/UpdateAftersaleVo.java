package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "修改售后单视图")
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAftersaleVo {

    @Min(1)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "售后原因")
    private String reason;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String detail;

    @ApiModelProperty(value = "联系人")
    private String consignee;

    @ApiModelProperty(value = "电话")
    private String mobile;
}
