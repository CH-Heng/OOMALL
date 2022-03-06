package cn.edu.xmu.oomall.goods.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/23 15:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo {
    @NotBlank(message="商品名称不能为空")
    private String name;
}
