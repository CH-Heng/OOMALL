package cn.edu.xmu.oomall.aftersale.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk
 * @date 2021/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRetVo {

    private Long id;

    private Long pid;

    private String name;

    private Byte state;
}
