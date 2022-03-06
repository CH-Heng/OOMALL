package cn.edu.xmu.oomall.order.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Gao Yanfeng
 * @date 2021/11/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "团购活动视图")
public class GroupOnActivityVo {

    private Long id;

    private String name;

    private ShopVo shop;

    private List<GroupOnStrategyVo> strategy;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @ApiModelProperty(value = "结束时间")
    private ZonedDateTime endTime;
}
