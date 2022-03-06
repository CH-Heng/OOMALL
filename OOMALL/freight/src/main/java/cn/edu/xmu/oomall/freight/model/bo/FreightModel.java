package cn.edu.xmu.oomall.freight.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreightModel implements Serializable {
    private Long id;
    private String name;
    private Byte defaultModel;
    private Byte type;
    private Integer unit;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
