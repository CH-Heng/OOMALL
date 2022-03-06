package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.customer.microservice.Vo.CouponActivityVo;
import cn.edu.xmu.oomall.customer.microservice.Vo.ProductVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CouponActivityFactory {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    public CouponActivityVo create(Long id) {
        CouponActivityVo couponActivityVo=new CouponActivityVo();

        couponActivityVo.setId(id);
        couponActivityVo.setQuantity(1);
        couponActivityVo.setName("1231");
        couponActivityVo.setImageUrl("1234124");
        couponActivityVo.setBeginTime(beginTime);
        couponActivityVo.setEndTime(endTime);

        return couponActivityVo;
    }
}
