package cn.edu.xmu.oomall.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="货品视图对象")
public class ProductVo {
    private Long id;
    private String name;
    private String imageUrl;
}
