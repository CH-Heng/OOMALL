package cn.edu.xmu.oomall.share.microservice.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="货品视图对象")
public class SimpleProductRetVo {
    private Long id;
    private String name;
    private String imageUrl;
}