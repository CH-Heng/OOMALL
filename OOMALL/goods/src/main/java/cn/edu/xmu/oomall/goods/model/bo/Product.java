package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.ProductVo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/14
 */
/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Data
@NoArgsConstructor
public class Product implements Serializable {
    private Long id;


    private Long shopId;

    private String shopName;

    private Long goodsId;


    private Long categoryId;
    private String categoryName;
    private Long freightId;

    private String skuSn;


    private String name;


    private Long originalPrice;


    private Long weight;


    private String imageUrl;

    private String barcode;


    private String unit;

    private String originPlace;

    private Long creatorId;

    private String creatorName;


    private Long modifierId;


    private String modifierName;

    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;


    private Byte state;

    private Long onsaleId;

    private Long price;

    private Integer quantity;

    public enum ProductState
    {
        /**
         * 共四种状态
         */
        DRAFT(0,"草稿"),
        OFFSHELF(1,"下架"),
        ONSHELF(2,"上架"),
        BANNED(3,"禁售中");
        private int code;
        private String state;
        ProductState(int code, String state) {
            this.code=code;
            this.state=state;
        }
        public int getCode(){
            return code;
        }

        public String getState(){return state;}
    }

}
