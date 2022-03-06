package cn.edu.xmu.oomall.freight.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * @author 高艺桐 22920192204199
 */
@Data
@NoArgsConstructor
public class PieceFreightRetVo implements VoObject {
    @ApiModelProperty(value = "主键")
    private Long id;
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
    @ApiModelProperty(value = "创建者")
    private SimpleUserRetVo creator;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;
    @ApiModelProperty(value = "修改者")
    private SimpleUserRetVo modifier;

    @Override
    public Object createVo() {
        return this;
    }
    @Override
    public Object createSimpleVo() {
        return this;
    }
}
