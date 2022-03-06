package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yujie lin
 * @date 2021/11/10
 */
@Data
public class NewOnSaleVo {

    @Min(0)
    private Long price;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    @Min(1)
    private Integer quantity;

    @NotNull
    private Byte type;

    @Min(1)
    private Integer maxQuantity;

    @Min(1)
    private Integer numKey;

    public NewOnSaleVo(){}

}
