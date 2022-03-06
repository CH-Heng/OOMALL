package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "售后单序号视图")
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleServiceSnVo {
    @NotBlank(message = "售后单序号不能为空")
    private String serviceSn;
}

