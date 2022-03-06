package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "店家售后运单信息视图")
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleShopLogSnVo {
    @NotBlank(message = "运单号不能为空")
    private String shopLogSn;
}
