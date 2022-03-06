package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author 王言光 22920192204292
 * @date 2021/12/7
 */
@Data
@NoArgsConstructor
public class ProductNewReturnVo {
    private Long id;
    private UserSimpleRetVo shop;
    private Long productId;
    private Long goodsId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private String unit;
    private String barCode;
    private String originPlace;
    private UserSimpleRetVo category;
    private UserSimpleRetVo createBy;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtCreate;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtModified;
    private UserSimpleRetVo modifiedBy;
}
