package cn.edu.xmu.oomall.liquidation.microservice.vo;

import cn.edu.xmu.oomall.liquidation.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PaymentRetVo {

    private Long id;

    private String tradeSn;

    private Long patternId;

    private Long amount;

    private Long actualAmount;

    private String documentId;

    private Byte documentType;

    private String descr;

    private LocalDateTime payTime;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Byte state;
}
