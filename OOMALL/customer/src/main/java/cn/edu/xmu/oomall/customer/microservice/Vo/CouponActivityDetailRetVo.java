package cn.edu.xmu.oomall.customer.microservice.Vo;

import cn.edu.xmu.oomall.customer.constant.Constants;
import cn.edu.xmu.oomall.customer.model.vo.SimpleCustomer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author wwk
 * @date 2021/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityDetailRetVo {

    private Long id;

    private String name;

    private SimpleShop shop;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime couponTime;

    private Integer quantity;

    private Byte quantityType;

    private Byte validTerm;

    private String imageUrl;

    private String strategy;

    private Byte state;

    private SimpleCustomer createBy;

    private SimpleCustomer modifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;
}
