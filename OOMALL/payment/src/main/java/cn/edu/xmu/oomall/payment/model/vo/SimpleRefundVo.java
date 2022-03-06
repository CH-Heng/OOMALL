package cn.edu.xmu.oomall.payment.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/19/17:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleRefundVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long paymentId;
    private Long amount;
    private String documentId;
    private Byte documentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime refundTime;
    private Byte state;
    private String descr;
}
