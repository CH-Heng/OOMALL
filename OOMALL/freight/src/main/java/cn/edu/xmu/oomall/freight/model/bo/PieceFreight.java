package cn.edu.xmu.oomall.freight.model.bo;


import cn.edu.xmu.oomall.freight.model.vo.SimpleUserRetVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @author 高艺桐 22920192204199
 */
@Data
@NoArgsConstructor
public class PieceFreight extends FreightItem implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "运输模板id")
    private Long freightModelId;
    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;
    @ApiModelProperty(value = "首件数")
    private Integer firstItems;
    @ApiModelProperty(value = "首费")
    private Long firstItemFreight;
    @ApiModelProperty(value = "续件数")
    private Integer additionalItems;
    @ApiModelProperty(value = "续费")
    private Long additionalItemsPrice;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;

    @Override
    public Long calculate(Integer quantity, Integer unit) {
        return firstItemFreight + calculatePart(firstItems, null, quantity, additionalItems, additionalItemsPrice);
    }
}
