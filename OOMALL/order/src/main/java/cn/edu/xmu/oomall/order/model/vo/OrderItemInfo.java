package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemInfo implements VoObject {
    private Long orderId;
    private Long shopId;
    private Long productId;
    private Integer state;
    private Long quantity;
    private Long price;
    private Long discountPrice;
    private Long point;
    private String name;
    private Long customerId;
    private Long onsaleId;
    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
