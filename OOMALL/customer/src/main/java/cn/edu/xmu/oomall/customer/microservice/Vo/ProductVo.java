package cn.edu.xmu.oomall.customer.microservice.Vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductVo implements VoObject{
    private Long id;
    private SimpleShop shop;
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
    private SimpleCategory category;
    private Boolean shareable;
    private Long freightId;

    @Override
    public Object createVo(){return this;}

    @Override
    public Object createSimpleVo(){return this;}
}
