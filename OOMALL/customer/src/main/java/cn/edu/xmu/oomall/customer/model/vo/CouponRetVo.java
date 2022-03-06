package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.customer.constant.Constants;
import cn.edu.xmu.oomall.customer.microservice.Vo.CouponActivityVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author wwk
 * @date 2021/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRetVo {
    private Long id;
    private String couponSn;
    private String name;
    private Long customerId;
    private CouponActivityVo activity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime endTime;
    private Byte state;
    private SimpleCustomer creator;
    private SimpleCustomer modifier;
    private Long modifierId;
    private String modifierName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.RETVO_DATE_TIME_FORMAT)
    private ZonedDateTime gmtModified;
}
