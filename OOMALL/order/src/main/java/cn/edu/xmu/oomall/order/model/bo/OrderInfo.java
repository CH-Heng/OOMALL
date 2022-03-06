package cn.edu.xmu.oomall.order.model.bo;

import cn.edu.xmu.oomall.order.model.vo.OrderPaymentRetVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private Order order;
    private List<OrderItem> orderItemList;
    private List<OrderPaymentRetVo> orderPaymentRetVos;
}
