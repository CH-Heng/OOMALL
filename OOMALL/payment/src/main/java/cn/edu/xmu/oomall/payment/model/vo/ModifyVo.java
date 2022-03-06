package cn.edu.xmu.oomall.payment.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @date 2021/12/08/20:39
 */
@Data
@ApiModel(description = "管理员修改支付信息视图")
@AllArgsConstructor
public class ModifyVo {
    @NotNull(message = "状态不能为空")
    Byte state;
    @NotBlank(message = "描述信息不能为空")
    String descr;
}
