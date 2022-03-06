package cn.edu.xmu.oomall.activity.microservice.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVo{
    @ApiModelProperty(value = "货品id")
    private Long id;
    @ApiModelProperty(value = "货品名称")
    private String name;
    @ApiModelProperty(value = "图片链接")
    private String imageUrl;

}