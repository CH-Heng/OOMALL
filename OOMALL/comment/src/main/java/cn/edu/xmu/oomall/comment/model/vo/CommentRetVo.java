package cn.edu.xmu.oomall.comment.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentRetVo{
    @ApiModelProperty(value = "评论id")
    private Long id;

    private Byte type;

    private String content;

    private Byte state;
    private SimpleUserRetVo post;
    private SimpleUserRetVo audit;
    private SimpleUserRetVo creator;
    private SimpleUserRetVo modifier;
    private LocalDateTime postTime;
    private LocalDateTime auditTime;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

}
