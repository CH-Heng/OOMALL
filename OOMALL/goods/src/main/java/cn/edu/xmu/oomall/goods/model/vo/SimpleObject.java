package cn.edu.xmu.oomall.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="简单对象,包括id和name")
public class SimpleObject {
    private Long id;
    private String name;
}
