package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author wwk
 * @date 2021/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPointVo {

    @NotNull(message = "返点数不能为空")
    private Long changePoint;
}
