package cn.edu.xmu.oomall.order.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:57
 **/
@Data
@NoArgsConstructor
public class OnSaleRetVo {
    private Long id;
    private Long price;
    private Integer quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime gmtModified;

    private SimpleProductRetVo product;
    private SimpleShopVo shop;
    private SimpleAdminUserBo creator;
    private SimpleAdminUserBo modifier;
}
