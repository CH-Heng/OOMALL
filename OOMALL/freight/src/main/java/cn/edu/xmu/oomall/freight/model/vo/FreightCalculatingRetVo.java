package cn.edu.xmu.oomall.freight.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gao Yanfeng
 * @date 2021/11/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingRetVo {
    private Long freightPrice;
    private Long productId;
}
