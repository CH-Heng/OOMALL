package cn.edu.xmu.oomall.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @date 2021/12/08/20:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRetVo {
    private Long id;
    private Long patternId;
    private String tradeSn;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private String actualAmount;
    private LocalDateTime payTime;
    private Byte state;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private UserSimpleRetVo adjust;
    private LocalDateTime adjustTime;
    private UserSimpleRetVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo modifier;
}
