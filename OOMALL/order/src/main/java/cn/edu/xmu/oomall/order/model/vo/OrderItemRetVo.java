package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRetVo implements VoObject {

    private Long productId;
    private String name;
    private Long quantity;
    private Long price;
    private Long point;
    private Long discountPrice;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
