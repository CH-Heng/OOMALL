package cn.edu.xmu.oomall.freight.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightModelInfoVo {
    @NotBlank(message = "模板名不能为空")
    private String name;
    @Min(value = 0,message = "最小值为0")
    private Integer unit;
    @NotNull
    private Byte type;
    @NotNull
    private Byte defaultModel;
}