package cn.edu.xmu.oomall.goods.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
/**
 * @author wwk's father
 * @date 2021/12/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftProductShopRetVo {
    private Long id;
    private Long productId;
    private SimpleObject shop;
    private Long goodsId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private SimpleObject category;
    private SimpleObject creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;
    private SimpleObject modifier;
}
