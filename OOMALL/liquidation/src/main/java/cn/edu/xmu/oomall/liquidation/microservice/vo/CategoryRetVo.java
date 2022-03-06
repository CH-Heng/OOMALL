package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk
 * @date 2021/11/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRetVo {

    private Long id;

    private String name;

    private Integer commissionRatio;
}
