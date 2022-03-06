package cn.edu.xmu.oomall.share.microservice.vo;

import cn.edu.xmu.oomall.share.model.vo.SimpleObjectVo;
import lombok.Data;

/**
 * 有修改
 */
@Data
public class ProductRetVo {
    private Long id;
    private SimpleObjectVo shop;
    private Long goodsId;
    private Long onsaleId;
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
    private SimpleObjectVo category;
    private Boolean shareable;
    private Long freightId;
}
