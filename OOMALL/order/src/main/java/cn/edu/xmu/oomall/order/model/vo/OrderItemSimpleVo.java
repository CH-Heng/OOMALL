package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemSimpleVo {
    @NotNull
    private Long productId;
    @NotNull
    private Long quantity;
    @NotNull
    private Long onsaleId;
}
