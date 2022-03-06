package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zijun Min
 * @description
 * @createTime 2021/11/24 15:21
 **/
@Data
@NoArgsConstructor
public class SimpleOnSaleRetVo implements VoObject {
    private Long id;
    private Long price;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Long activityId;
    private Long shareActId;
    private Byte type;
    private Byte state;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
