package cn.edu.xmu.oomall.goods.model.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/29
 */
@Data
public class QuantityVo {

    @Min(1)
    private  Integer quantity;
}
