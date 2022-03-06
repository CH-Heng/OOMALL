package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVo {
    @NotNull
    private String message;
}
