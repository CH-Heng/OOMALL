package cn.edu.xmu.oomall.activity.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 17:36
 */
@Data
@ToString
public class    ShareActivityVo {
    @NotBlank(message="预售活动名称不能为空")
    private String name;

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

    @Valid
    private List<StrategyVo> strategy;
}
