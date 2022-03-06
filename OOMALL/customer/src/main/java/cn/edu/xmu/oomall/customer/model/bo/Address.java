package cn.edu.xmu.oomall.customer.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Long id;
    private Long customerId;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private Byte default1;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
