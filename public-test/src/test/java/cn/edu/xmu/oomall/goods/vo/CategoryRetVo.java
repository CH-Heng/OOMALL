package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 商品分类RetVo
 *
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/18
 */
@Data
@NoArgsConstructor
public class CategoryRetVo {
    @ApiModelProperty(value = "分类id")
    private Long id;
    @ApiModelProperty(value = "佣金率")
    private Integer commissionRatio;
    @ApiModelProperty(value = "分类名")
    private String name;
    @ApiModelProperty(value = "创建人")
    private UserSimpleRetVo creator;
    @ApiModelProperty(value = "修改人")
    private UserSimpleRetVo modifier;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtModified;
}
