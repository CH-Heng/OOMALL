package cn.edu.xmu.oomall.goods.vo;

import cn.edu.xmu.oomall.privilege.vo.UserSimpleRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author Xusheng Wang
 * @date 2021/12/17
 * @studentId 34520192201587
 */
@Data
public class ShopAccountRetVo {

    private Long  id;

    @ApiModelProperty(value = "支付渠道")
    private Byte type;

    @ApiModelProperty(value = "账户号")
    private String account;

    @ApiModelProperty(value = "账户名称")
    private String name;

    @ApiModelProperty(value = "汇入优先级")
    private Byte priority;

    private UserSimpleRetVo creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;
    private UserSimpleRetVo modifier;
}
