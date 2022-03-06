package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "买家售后退款视图")
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleRefundVo {
    private String serviceSn;

    private Long orderItemId;

    private Long price;

    private Long point;
}
