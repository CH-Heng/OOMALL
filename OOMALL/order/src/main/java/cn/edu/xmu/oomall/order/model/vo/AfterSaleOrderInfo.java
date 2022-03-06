package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author RenJieZheng 22920192204334
 * @date 2021/12/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSaleOrderInfo {
    @Valid
    private List<OrderItemSimpleVo>orderItems;
    private Long customerId;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
}
