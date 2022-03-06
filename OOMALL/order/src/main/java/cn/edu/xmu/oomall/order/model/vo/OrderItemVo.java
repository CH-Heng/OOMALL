package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVo {
    @NotNull
    private Long productId;
    @NotNull
    private Long onsaleId;
    @Min(0)
    private Long quantity;
    private Long couponActivityId;
    private Long couponId;
}
