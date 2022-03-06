package cn.edu.xmu.oomall.goods.microservice.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 17:36
 */
@Data
@ToString
public class ShareActivityRetVo {
    private Long id;

    private String name;

    private Byte state;
}
