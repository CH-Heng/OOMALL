package cn.edu.xmu.oomall.activity.model.vo;
import cn.edu.xmu.oomall.activity.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.activity.microservice.vo.SimpleShopVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
public class FullAdvanceSaleRetVo {
    @ApiModelProperty(value = "预售活动id")
    private Long id;

    @ApiModelProperty(value = "预售活动名称")
    private String name;

    @ApiModelProperty(value = "店铺")
    private SimpleShopVo shop;

    @ApiModelProperty(value = "货品")
    private ProductVo product;

    @ApiModelProperty(value = "支付尾款时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime payTime;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "订金")
    private Long advancePayPrice;

    @ApiModelProperty(value = "创建者")
    private SimpleUserRetVo creator;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;

    @ApiModelProperty(value = "修改者")
    private SimpleUserRetVo modifier;

    @ApiModelProperty(value = "状态")
    private Byte state;
}