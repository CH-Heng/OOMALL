package cn.edu.xmu.oomall.freight.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yujie Lin
 * @date 2021/12/8 17:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDetailRetVo {
    private Long id;
    private Long pid;
    private String name;
    private Byte state;

    }
