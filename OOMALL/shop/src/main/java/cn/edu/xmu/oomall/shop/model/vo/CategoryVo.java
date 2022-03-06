package cn.edu.xmu.oomall.shop.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 商品分类Vo
 *
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/12
 */
@Data
@NoArgsConstructor
public class CategoryVo {
    @ApiModelProperty(value = "用户名")
    private String name;

    private Integer commissionRatio;
}
