package cn.edu.xmu.oomall.freight.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/16
 */
@Data
@NoArgsConstructor
public class WeightFreight extends FreightItem implements Serializable {

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
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    @Override
    public Long calculate(Integer weight, Integer unit) {
        return firstWeightFreight +
                calculatePart(firstWeight, 10_000, weight, unit, tenPrice) +
                calculatePart(10_000, 50_000, weight, unit, fiftyPrice) +
                calculatePart(50_000, 100_000, weight, unit, hundredPrice) +
                calculatePart(100_000, 300_000, weight, unit, trihunPrice) +
                calculatePart(300_000, null, weight, unit, abovePrice)
                ;
    }
}
