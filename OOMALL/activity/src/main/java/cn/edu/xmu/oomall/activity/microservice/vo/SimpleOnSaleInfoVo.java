package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOnSaleInfoVo {
    @ApiModelProperty(value = "销售id")
    private Long id;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    private Long shareActId;

    @ApiModelProperty(value = "类型")
    private Byte type;

    private Byte state;
}
