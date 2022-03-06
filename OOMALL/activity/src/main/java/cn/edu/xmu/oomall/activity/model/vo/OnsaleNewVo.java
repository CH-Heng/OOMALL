package cn.edu.xmu.oomall.activity.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OnsaleNewVo {
    private Long price;
    private Integer numKey;
    private Integer maxQuantity;
    private Integer quantity;
}
