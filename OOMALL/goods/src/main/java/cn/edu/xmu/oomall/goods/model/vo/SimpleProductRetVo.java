package cn.edu.xmu.oomall.goods.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:20
 **/
@Data
@NoArgsConstructor
public class SimpleProductRetVo {
    private Long id;
    private String name;
    private String imageUrl;
}
