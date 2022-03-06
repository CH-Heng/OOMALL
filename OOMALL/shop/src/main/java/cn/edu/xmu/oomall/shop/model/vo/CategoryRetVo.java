package cn.edu.xmu.oomall.shop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    private SimpleUserRetVo creator;
    @ApiModelProperty(value = "修改人")
    private SimpleUserRetVo modifier;
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtCreate;
    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtModified;

}
