package cn.edu.xmu.oomall.customer.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {
    private Long id;
    private Long customerId;
    private Long productId;
    private Long quantity;
    private Long price;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
