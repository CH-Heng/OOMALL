package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.share.microservice.vo.StrategyVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimpleSuccessfulShareRetVo {
    private Long id;

    private Long sharerId;

    private List<StrategyVo> strategy;
}
