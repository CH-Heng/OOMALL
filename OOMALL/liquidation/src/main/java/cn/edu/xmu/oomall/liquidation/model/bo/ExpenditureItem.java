package cn.edu.xmu.oomall.liquidation.model.bo;

import lombok.Data;

import java.time.LocalDateTime;




/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
public class ExpenditureItem {

    private Long id;

    private Long liquidId;

    private Long refundId;

    private Long shopId;

    private String shopName;

    private Long revenueId;

    private Long productId;

    private String productName;

    private Long orderId;

    private Long orderitemId;

    private Long amount;

    private Long expressFee;

    private Long commission;

    private Long point;

    private Long sharerId;

    private Long shopRevenue;

    private Integer quantity;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
