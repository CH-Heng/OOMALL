package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Data
@NoArgsConstructor
public class FreightModelRetVo {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Byte defaultModel;
    private UserSimpleRetVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo modifier;
}