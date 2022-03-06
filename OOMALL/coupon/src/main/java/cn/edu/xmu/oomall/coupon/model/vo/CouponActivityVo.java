package cn.edu.xmu.oomall.coupon.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.ZonedDateTime;

/**
 * @author RenJieZheng 22920192204334
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CouponActivityVo implements VoObject {
    @Length(max = 100,message = "name字符串最大长度为100")
    private String name;
    @Min(value = 0,message = "quantity最小值为0")
    private Integer quantity;
    @Min(value = 0,message = "quantityType最小值为0")
    @Max(value = 1,message = "quantityType最大值为1")
    private Byte quantityType;
    @Min(value = 0,message = "validTerm最小值为0")
    @Max(value = 1,message = "validTerm最大值为1")
    private Byte validTerm;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime couponTime;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;
    private String strategy;
    private Integer numKey;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
