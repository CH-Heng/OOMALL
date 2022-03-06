package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoVo {
    private List<OrderItemVo>orderItems;
    private String consignee;
    @NotNull
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Long advancesaleId;
    private Long grouponId;
    @Min(0)
    private Long point;
    /**
     * 订单编号，由Common.getSeqNum生成
     */
    private String orderSn;
}
