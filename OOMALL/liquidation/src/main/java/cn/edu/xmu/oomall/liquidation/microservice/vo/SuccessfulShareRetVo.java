package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessfulShareRetVo {

    private Long id;

    private Long sharerId;

    private List<StrategyRetVo> strategy;
}
