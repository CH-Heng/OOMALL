package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyRetVo {

    private Long quantity;

    private Integer percentage;
}
