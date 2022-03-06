package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRetVo implements VoObject {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Integer state;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private Long grouponId;
    private Long advancesaleId;
    private String shipmentSn;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
