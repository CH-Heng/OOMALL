package cn.edu.xmu.oomall.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo<T> {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    private Integer pages;
    private List<T> list;

}
