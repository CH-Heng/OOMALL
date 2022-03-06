package cn.edu.xmu.oomall.comment.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentSelectRetVo {
    private Long page;
    private Long pageSize;
    private Long total;
    private Long pages;
    private List<Object> list=new ArrayList<>();
}
