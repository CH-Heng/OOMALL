package cn.edu.xmu.oomall.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/14/0:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPatternVo {
    @NotNull(message = "支付方式不能为空")
    Long payPattern;
}
