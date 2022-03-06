package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleCreatedVo {
    @Min(0)
    private Long price;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    private Integer quantity;

    private Long activityId;

    @NotNull
    private Byte type;

    private Integer maxQuantity;

    private Integer numKey;
}