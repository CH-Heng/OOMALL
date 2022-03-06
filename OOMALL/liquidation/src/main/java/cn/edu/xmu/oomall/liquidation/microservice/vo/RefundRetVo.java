package cn.edu.xmu.oomall.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRetVo {

    private Long id;

    private Long paymentId;

    private String tradeSn;

    private Long patternId;

    private Long amount;

    private String documentId;

    private Byte documentType;

    private Byte state;
}
