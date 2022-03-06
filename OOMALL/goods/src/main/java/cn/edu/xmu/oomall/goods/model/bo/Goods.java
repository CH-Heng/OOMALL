package cn.edu.xmu.oomall.goods.model.bo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 黄添悦
 **/
/**
 * @author 王文飞
 */
@Data
@NoArgsConstructor
public class Goods implements Serializable {
    private Long id;
    private String name;
    private Long shopId;
    private List<Product> productList;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifierId;
    private String modifierName;
}
