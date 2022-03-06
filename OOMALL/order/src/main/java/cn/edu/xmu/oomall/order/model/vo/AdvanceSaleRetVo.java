package cn.edu.xmu.oomall.order.model.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.ZonedDateTime;

/**
 * @author Jiawei Zheng
 * @date 2021-11-30
 */
@Data
public class AdvanceSaleRetVo {
    @ApiModelProperty(value = "预售活动id")
    private Long id;

    @ApiModelProperty(value = "预售活动名称")
    private String name;

    @ApiModelProperty(value = "店铺")
    private SimpleShopVo shop;

    @ApiModelProperty(value = "货品")
    private SimpleProductRetVo product;

    @ApiModelProperty(value = "支付尾款时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime payTime;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "数量")
    private Long quantity;

    @ApiModelProperty(value = "订金")
    private Long advancePayPrice;
}