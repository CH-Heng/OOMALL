package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceSaleModifyVo {

    @ApiModelProperty(value = "活动名")
    private String name;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @ApiModelProperty(value = "结束时间")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "支付首款时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime payTime;

    @ApiModelProperty(value = "首款金额")
    private Long advancePayPrice;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "价格")
    private Long price;
}
