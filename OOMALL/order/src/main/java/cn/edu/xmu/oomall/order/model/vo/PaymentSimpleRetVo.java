package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSimpleRetVo implements VoObject{
    private Long id;
    private String tradeSn;
    private Long patternId;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private Long actualAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime payTime;
    private Byte state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}