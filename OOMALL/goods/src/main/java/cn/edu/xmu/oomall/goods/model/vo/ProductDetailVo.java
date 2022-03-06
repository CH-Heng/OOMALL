package cn.edu.xmu.oomall.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@ApiModel(value = "Product详细视图")
public class ProductDetailVo {
    private String skuSn;
    @NotBlank
    @NotNull
    private String name;
    @Min(0)
    private Long originalPrice;
    @Min(0)
    private Long weight;
    private Long categoryId;
    private Long goodsId;
    private String barCode;
    private String unit;
    private String originPlace;

}
