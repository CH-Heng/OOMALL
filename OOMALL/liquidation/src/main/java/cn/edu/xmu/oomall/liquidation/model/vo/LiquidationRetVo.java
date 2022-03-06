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
public class LiquidationRetVo {
    private Long id;
    private SimpleObject shop;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime liquidDate;
    private Long expressFee;
    private Long commission;
    private Long shopRevenue;
    private Long point;
    private Byte state;
    private SimpleObject creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtModified;
    private SimpleObject modifier;
}