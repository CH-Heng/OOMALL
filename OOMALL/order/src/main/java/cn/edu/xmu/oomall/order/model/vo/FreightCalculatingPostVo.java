package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;


/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingPostVo {
    private Long productId;

    private Long quantity;

    private Long freightId;

    private Long weight;
}
