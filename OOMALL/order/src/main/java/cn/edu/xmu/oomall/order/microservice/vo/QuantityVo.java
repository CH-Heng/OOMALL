package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityVo {

    @Min(1)
    private  Integer quantity;
}
