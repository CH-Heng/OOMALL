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
 * @date 2021/12/03/15:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPatternRetVo {
    private Long id;
    private String name;
    private Byte state;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String className;
    private UserSimpleRetVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo modifier;
}
