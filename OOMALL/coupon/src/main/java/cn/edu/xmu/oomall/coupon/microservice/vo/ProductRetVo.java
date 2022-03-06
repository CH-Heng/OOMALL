package cn.edu.xmu.oomall.coupon.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 王言光 22920192204292
 * @modified Zijun Min 22920192204257
 * @date 2021/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRetVo {
    private Long id;
    private ShopVo shop;
    private Long goodsId;
    private Long onSaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Long price;
    private Long quantity;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private CategoryVo category;
    private Boolean shareable;
    private Long freightId;
}
