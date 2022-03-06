package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceSaleVo{
    @ApiModelProperty(value = "价格")
    @Min(0)
    @NotNull(message = "价格不能为空")
    private Long price;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @NotNull(message = "开始时间不能为空")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "数量")
    @Min(0)
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @ApiModelProperty(value = "预售活动名称")
    @NotBlank(message = "预售活动名称不能为空")
    private String name;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @NotNull(message = "尾款支付时间不能为空")
    private ZonedDateTime payTime;

    @ApiModelProperty(value = "订金")
    @DecimalMin("0")
    @Min(0)
    @NotNull(message = "订金不能为空")
    private Long advancePayPrice;
}