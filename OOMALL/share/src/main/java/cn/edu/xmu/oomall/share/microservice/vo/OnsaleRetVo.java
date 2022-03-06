package cn.edu.xmu.oomall.share.microservice.vo;

import cn.edu.xmu.oomall.share.constant.Constants;
import cn.edu.xmu.oomall.share.model.vo.SimpleObjectVo;
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
public class OnsaleRetVo {
    private Long id;
    private Long price;
    private Integer quantity;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;
    private Byte state;

    private SimpleProductRetVo product;
    private SimpleObjectVo shop;
    private SimpleObjectVo creator;
    private SimpleObjectVo modifier;
}
