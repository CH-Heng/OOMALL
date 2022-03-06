package cn.edu.xmu.oomall.shop.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopRetVo {
    private Long id;
    private String name;
    private Long deposit;
    private Long depositThreshold;
    private Byte state;
    private SimpleUserRetVo creator;
    private SimpleUserRetVo modifier;
    private String gmtCreated;
    private String gmtModified;
}
