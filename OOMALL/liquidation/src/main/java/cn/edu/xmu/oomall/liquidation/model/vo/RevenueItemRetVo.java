package cn.edu.xmu.oomall.liquidation.model.vo;

import cn.edu.xmu.oomall.liquidation.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author wyg
 * @date 2021/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueItemRetVo {
    private Long id;
    private SimpleObject shop;
    private SimpleObject product;
    private Long amount;
    private Integer quantity;
    private Long commission;
    private Long point;
    private Long shopRevenue;
    private Long expressFee;
    private SimpleObject creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtModified;
    private SimpleObject modifierId;
}
