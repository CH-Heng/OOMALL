package cn.edu.xmu.oomall.payment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 17:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationRetVo {
    private Integer success;
    private Integer error;
    private Integer extra;
}
