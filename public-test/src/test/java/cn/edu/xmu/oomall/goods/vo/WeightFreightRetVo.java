package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/16
 */
@Data
@NoArgsConstructor
public class WeightFreightRetVo {

    private Long id;
    private Long freightModelId;
    private Integer firstWeight;
    private Long firstWeightFreight;
    private Long tenPrice;
    private Long fiftyPrice;
    private Long hundredPrice;
    private Long trihunPrice;
    private Long abovePrice;
    private Long regionId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo creator;
    private UserSimpleRetVo modifier;
}
