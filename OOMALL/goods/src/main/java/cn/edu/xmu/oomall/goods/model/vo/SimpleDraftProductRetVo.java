package cn.edu.xmu.oomall.goods.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk's father
 * @date 2021/12/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleDraftProductRetVo {
    private Long id;
    private Long productId;
    private Long goodsId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private String unit;
    private String barCode;
}
