package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRetVo {

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
}
