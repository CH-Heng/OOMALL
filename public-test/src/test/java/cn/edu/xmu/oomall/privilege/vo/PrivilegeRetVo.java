package cn.edu.xmu.oomall.privilege.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限返回VO
 * @author Ming Qiu
 * @date Created in 2020/11/3 23:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeRetVo {

    private Long id;

    private String name;

    private String url;

    private Byte requestType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime gmtModified;

    private UserSimpleRetVo creator;

    private UserSimpleRetVo modifier;

    private Integer sign;

}
