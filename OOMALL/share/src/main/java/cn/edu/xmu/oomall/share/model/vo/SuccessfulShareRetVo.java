package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.share.microservice.vo.SimpleProductRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static cn.edu.xmu.oomall.share.constant.Constants.DATE_TIME_FORMAT;

@Data
public class SuccessfulShareRetVo {
    private Long id;

    private SimpleProductRetVo product;

    private Long sharerId;

    private Byte state;

    private Long customerId;

    SimpleObjectVo creator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;

    SimpleObjectVo modifier;
}
