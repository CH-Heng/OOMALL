package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/21/18:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSeperateOrderVo {
    private String documentId;
    private Byte documentType;
}
