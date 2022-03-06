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
public class CustomerRetVo {

    private Long id;

    private String userName;

    private String name;

    private Long point;

    private Byte state;

    private String email;

    private String mobile;
}
