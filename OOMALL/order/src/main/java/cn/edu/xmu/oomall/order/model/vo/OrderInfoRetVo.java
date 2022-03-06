package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.order.microservice.vo.CustomerSimpleRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.SimpleObjectRetVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoRetVo implements VoObject {
    private Long id;
    private String orderSn;
    private CustomerSimpleRetVo customer;
    private SimpleObjectRetVo shop;
    private Long pid;
    private Integer state;
    private LocalDateTime confirmTime;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private String message;
    private Long regionId;
    private String address;
    private String mobile;
    private String consignee;
    private Long grouponId;
    private Long advancesaleId;
    private String shipmentSn;
    private List<OrderItemRetVo>orderItem;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
