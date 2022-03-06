package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/09/9:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundSimpleRetVo {
    private Long id;
    private Long paymentId;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private Byte state;
    private String documentId;
    private Byte documentType;
}
