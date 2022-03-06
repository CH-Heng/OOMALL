package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateFieldVo {
    private String consignee;
    @Min(0)
    private Long regionId;
    private String address;
    private String mobile;
}
