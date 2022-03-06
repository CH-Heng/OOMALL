package cn.edu.xmu.oomall.share.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Share {
    private Long id;

    private Long sharerId;

    private Long shareActId;

    private Long productId;

    private Long onsaleId;

    private Long quantity;

    private Byte state;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
