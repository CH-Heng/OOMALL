package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingVo {
    private Long freightPrice;
    private Long productId;
}