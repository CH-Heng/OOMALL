package cn.edu.xmu.oomall.liquidation.model.vo;

import cn.edu.xmu.oomall.liquidation.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartInfoVo {

    @DateTimeFormat(pattern = Constants.BODAY_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.BODAY_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = Constants.BODAY_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.BODAY_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;
}
