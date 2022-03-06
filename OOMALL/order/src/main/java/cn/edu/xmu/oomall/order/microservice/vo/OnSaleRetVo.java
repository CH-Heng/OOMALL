package cn.edu.xmu.oomall.order.microservice.vo;


import cn.edu.xmu.oomall.order.microservice.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleRetVo {
    private Long id;
    private Long price;
    private Integer quantity;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;
    @DateTimeFormat(pattern = Constants.RETVO_DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;
    private Byte state;

    private SimpleProductRetVo product;
    private SimpleShopVo shop;
    private SimpleAdminUserVo creator;
    private SimpleAdminUserVo modifier;
}
