package cn.edu.xmu.privilegegateway.privilegeservice.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserVo {
    private Long id;
    private String name;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo creator;
    private UserSimpleRetVo modifier;
    private Byte sign;
}
