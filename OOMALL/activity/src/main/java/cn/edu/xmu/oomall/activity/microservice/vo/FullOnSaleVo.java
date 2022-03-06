package cn.edu.xmu.oomall.activity.microservice.vo;

import cn.edu.xmu.oomall.activity.model.vo.SimpleUserRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullOnSaleVo {

    @ApiModelProperty(value = "OnsaleId")
    private Long id;

    @ApiModelProperty(value = "店铺")
    private SimpleShopVo shop;

    @ApiModelProperty(value = "货品")
    private ProductVo product;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "类型")
    private Byte type;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty(value = "分享活动id")
    private Long shareActId;

    @ApiModelProperty(value = "创建者")
    private SimpleUserRetVo creator;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtModified;

    @ApiModelProperty(value = "修改者")
    private SimpleUserRetVo modifier;

    private Byte state;
}