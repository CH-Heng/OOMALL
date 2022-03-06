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
public class ProductRetVo {

    private Long id;

    private SimpleObjectVo shop;

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

    private CategoryRetVo category;

    private Boolean shareable;

}
