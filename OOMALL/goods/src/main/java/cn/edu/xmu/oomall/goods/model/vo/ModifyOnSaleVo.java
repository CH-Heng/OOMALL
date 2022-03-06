package cn.edu.xmu.oomall.goods.model.vo;

import cn.edu.xmu.oomall.goods.constant.Constants;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author YuJie 22920192204242
 * @date 2021/11/15
 */
@Data
public class ModifyOnSaleVo {

    private Long price;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    private Integer quantity;

    private Integer maxQuantity;

    private Integer numKey;

    private Long shareActId;

    public void updateOnsaleByModify(OnSale onsale){
        OnSale modifyOnsale=cloneVo(this,OnSale.class);
        if(modifyOnsale.getBeginTime()!=null){
            onsale.setBeginTime(modifyOnsale.getBeginTime());
        }
        if(modifyOnsale.getEndTime()!=null){
            onsale.setEndTime(modifyOnsale.getEndTime());
        }
        if(modifyOnsale.getPrice()!=null){
            onsale.setPrice(modifyOnsale.getPrice());
        }
        if(modifyOnsale.getQuantity()!=null){
            onsale.setQuantity(modifyOnsale.getQuantity());
        }
        if(modifyOnsale.getMaxQuantity()!=null){
            onsale.setMaxQuantity(modifyOnsale.getMaxQuantity());
        }
        if(modifyOnsale.getNumKey()!=null){
            onsale.setNumKey(modifyOnsale.getNumKey());
        }
        if(modifyOnsale.getShareActId()!=null){
            onsale.setShareActId(modifyOnsale.getShareActId());
        }
    }

}
