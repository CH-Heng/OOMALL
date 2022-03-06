package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "指定新订单的订单内容物的信息")
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleOrderItemVo {

    @NotBlank(message = "货品id不能为空")
    private Long productId;
    @NotBlank(message = "上架id不能为空")
    private Long onsaleId;
    @NotBlank(message = "数量不能为空")
    @Min(0)
    private Long quantity;
}
