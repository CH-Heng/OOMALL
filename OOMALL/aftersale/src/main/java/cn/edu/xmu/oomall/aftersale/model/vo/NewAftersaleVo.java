package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "新建售后单视图")
@NoArgsConstructor
@AllArgsConstructor
public class NewAftersaleVo {

    @NotNull(message = "售后类型不能为空")
    @Max(2)
    @Min(0)
    @ApiModelProperty(value = "售后类型")
    private Byte type;  //0换货，1退货，2维修

    @NotNull(message = "数量不能为空")
    @Min(1)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    @NotBlank(message = "售后原因不能为空")
    @ApiModelProperty(value = "售后原因")
    private String reason;

    @NotNull(message = "地区id不能为空")
    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @NotBlank(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址")
    private String detail;

    @NotBlank(message = "联系人不能为空")
    @ApiModelProperty(value = "联系人")
    private String consignee;

    @NotBlank(message = "电话不能为空")
    @ApiModelProperty(value = "电话")
    private String mobile;
}
