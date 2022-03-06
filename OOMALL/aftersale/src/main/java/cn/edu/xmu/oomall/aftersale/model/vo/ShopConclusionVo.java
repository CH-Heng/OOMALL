package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "店铺处理售后意见视图")
@NoArgsConstructor
@AllArgsConstructor
public class ShopConclusionVo {
    @NotNull(message = "处理结果不能为空")
    private Boolean confirm;

    private String conclusion;
}
