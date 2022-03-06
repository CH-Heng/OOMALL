package cn.edu.xmu.oomall.freight.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * @author Gao Yanfeng
 * @date 2021/11/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingPostVo {
    @Min(1)
    private Long productId;

    @Min(0)
    private Integer quantity;

    @Min(1)
    private Long freightId;

    @Min(0)
    private Integer weight;
}
