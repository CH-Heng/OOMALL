package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.share.microservice.vo.OnsaleRetVo;
import cn.edu.xmu.oomall.share.microservice.vo.SimpleProductRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static cn.edu.xmu.oomall.share.constant.Constants.DATE_TIME_FORMAT;

@Data
public class ShareRetVo {
    private Long id;

    SimpleObjectVo sharer;

    private SimpleProductRetVo product;

    private Long quantity;

    SimpleObjectVo creator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;

    SimpleObjectVo modifier;
}
