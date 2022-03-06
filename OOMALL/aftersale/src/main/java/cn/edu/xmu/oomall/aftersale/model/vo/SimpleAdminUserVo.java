package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "简单管理员视图")
public class SimpleAdminUserVo {
    @ApiModelProperty(value = "管理员id")
    private Long id;
    @ApiModelProperty(value = "管理员name")
    private String name;
}
