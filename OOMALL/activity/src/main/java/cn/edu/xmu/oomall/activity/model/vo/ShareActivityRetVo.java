package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 17:36
 */
@Data
@ToString
public class ShareActivityRetVo {
    private Long id;

    private String name;

    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;

    private List<StrategyVo> strategy;
}
