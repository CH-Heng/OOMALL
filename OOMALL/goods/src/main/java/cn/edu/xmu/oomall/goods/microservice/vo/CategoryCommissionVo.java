package cn.edu.xmu.oomall.goods.microservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author wyg
 * @Date 2021/11/22
 */
@Data
@NoArgsConstructor
public class CategoryCommissionVo {
    private Long id;
    private String name;
    private Integer commissionRatio;
}