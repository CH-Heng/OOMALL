package cn.edu.xmu.oomall.activity.model.vo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleAdvanceSaleRetVo{

    @ApiModelProperty(value = "活动id")
    private Long id;

    @ApiModelProperty(value = "活动名")
    private String name;
}