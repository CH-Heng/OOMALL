package cn.edu.xmu.oomall.payment.model.vo;

import cn.edu.xmu.oomall.payment.model.po.ErrorPaymentPo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 16:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorPaymentRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long income;
    private Long expenditure;
    private String documentId;
    private Byte state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime time;
    private String descr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime adjustTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;

    private SimpleAdjustRetVo adjust;
    private UserSimpleRetVo creator;
    private UserSimpleRetVo modifier;

    public void updateByPo(ErrorPaymentPo ErrorPaymentPo){
        this.adjust.setId(ErrorPaymentPo.getAdjustId());
        this.adjust.setName(ErrorPaymentPo.getAdjustName());
        this.creator.setId(ErrorPaymentPo.getCreatorId());
        this.creator.setName(ErrorPaymentPo.getCreatorName());
        this.modifier.setId(ErrorPaymentPo.getModifierId());
        this.modifier.setName(ErrorPaymentPo.getModifierName());
    }
}
