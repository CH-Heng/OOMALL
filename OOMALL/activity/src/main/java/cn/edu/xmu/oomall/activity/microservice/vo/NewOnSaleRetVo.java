package cn.edu.xmu.oomall.activity.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.ZonedDateTime;

/**
 * @author yujie lin
 * @date 2021/11/10
 */
@Data
public class NewOnSaleRetVo {
    private Long id;

    @Min(0)
    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime endTime;

    @Min(1)
    private Integer quantity;

    private Long activityId;

    private Long shareActId;

    private Byte type;

    private Byte state;

}
