package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.microservice.vo.CategoryCommissionVo;
import cn.edu.xmu.oomall.goods.microservice.vo.SimpleCategoryVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalProductRetVo {
    private Long id;
    private Long shopId;
    private Long goodsId;
    private Long onSaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Long price;
    private Integer quantity;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private CategoryCommissionVo category;
    private Boolean shareable;
}
