package cn.edu.xmu.oomall.goods.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightModelRetVo {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Byte defaultModel;
    private SimpleUserRetVo creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;
    private SimpleUserRetVo modifier;
}