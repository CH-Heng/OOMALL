package cn.edu.xmu.oomall.order.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/18/14:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundVo {
    private String documentId;
    private Byte documentType;
    private Long amount;
}
