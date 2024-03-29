package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/19/15:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundVo {
    private String documentId;
    private Byte documentType;
    private Long amount;
}
