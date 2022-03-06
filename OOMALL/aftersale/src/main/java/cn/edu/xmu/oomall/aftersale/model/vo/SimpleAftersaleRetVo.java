package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@ApiModel(value = "售后单返回视图")
@AllArgsConstructor
@NoArgsConstructor
public class SimpleAftersaleRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    private String serviceSn;
    private Byte type;
    private String reason;
    private Long price;
    private Long quantity;
    private String customerLogSn;   //寄回运单号
    private String shopLogSn;       //寄出运单号
    private Byte state;
}
