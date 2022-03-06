package cn.edu.xmu.oomall.goods.model.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@ApiModel(value = "goods返回视图")
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSimpleRetVo {

    private Long id;
    private String name;
    private List<SimpleProductRetVo> productList;

}
