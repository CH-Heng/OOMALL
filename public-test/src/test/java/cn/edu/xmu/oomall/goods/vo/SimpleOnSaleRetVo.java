package cn.edu.xmu.oomall.goods.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/24 15:21
 **/
@Data
@NoArgsConstructor
public class SimpleOnSaleRetVo{
    private Long id;
    private Long price;
    private ZonedDateTime beginTime;
    private ZonedDateTime endTime;
    private Integer quantity;
    private Long activityId;
    private Long shareActId;
    private Byte type;
    private Byte state;
}
