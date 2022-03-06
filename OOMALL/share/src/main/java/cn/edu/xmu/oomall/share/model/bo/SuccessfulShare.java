package cn.edu.xmu.oomall.share.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuccessfulShare {
    private Long id;

    private Long shareId;

    private Long sharerId;

    private Long productId;

    private Long onsaleId;

    private Long customerId;

    //0 有效，1 已清算，2 无效
    private Byte state;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
