package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:59
 **/
@Data
@NoArgsConstructor
public class SimpleAdminUserVo {
    private Long id;
    private String name;
    private Boolean sign;
}
