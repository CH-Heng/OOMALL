package cn.edu.xmu.oomall.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 16:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPaymentUpdateVo {
    @Min(1)@Max(1)
    private Byte state;
    private String descr;
}
