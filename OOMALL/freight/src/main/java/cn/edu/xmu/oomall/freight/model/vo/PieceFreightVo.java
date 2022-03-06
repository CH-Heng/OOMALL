package cn.edu.xmu.oomall.freight.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 高艺桐 22920192204199
 */
@Data
@NoArgsConstructor
public class PieceFreightVo {
    @NotNull(message="抵达地区码不能为空")
    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    @NotNull(message="首件数不能为空")
    @Min(value = 0,message = "首件数不能为负")
    @ApiModelProperty(value = "首件数")
    private Integer firstItems;

    @NotNull(message="首费不能为空")
    @Min(value = 0,message = "首费不能为负")
    @ApiModelProperty(value = "首费")
    private Long firstItemFreight;

    @NotNull(message="续件数不能为空")
    @Min(value = 0,message = "续件数不能为负")
    @ApiModelProperty(value = "续件数")
    private Integer additionalItems;

    @NotNull(message="续费不能为空")
    @Min(value = 0,message = "续费不能为负")
    @ApiModelProperty(value = "续费")
    private Long additionalItemsPrice;
}
