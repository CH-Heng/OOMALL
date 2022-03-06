package cn.edu.xmu.oomall.payment.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 15:38
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleErrorPaymentRetVo implements VoObject {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long income;
    private Long expenditure;
    private Byte state;
    private Long documentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime time;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
