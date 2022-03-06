package cn.edu.xmu.oomall.comment.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "评论审核结果视图")
@NoArgsConstructor
@AllArgsConstructor
public class CommentConclusionVo {
    @ApiModelProperty(value = "评论审核结果")
    @NotNull
    private boolean conclusion;

    public boolean getConclusion(){
        return this.conclusion;
    }
}
