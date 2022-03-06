package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 */
@Data
@NoArgsConstructor
public class PieceFreightRetVo {
    private Long id;
    private Long regionId;
    private Integer firstItems;
    private Long firstItemFreight;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private UserSimpleRetVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserSimpleRetVo modifier;
}
