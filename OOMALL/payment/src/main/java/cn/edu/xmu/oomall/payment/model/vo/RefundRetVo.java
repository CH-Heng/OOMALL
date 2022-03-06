package cn.edu.xmu.oomall.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/11/22:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long paymentId;
    private Long amount;
    private Byte state;
    private String documentId;
    private Byte documentType;
    private String descr;
    private UserSimpleRetVo adjust;
    private LocalDateTime adjustTime;
    private UserSimpleRetVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo modifier;

}
