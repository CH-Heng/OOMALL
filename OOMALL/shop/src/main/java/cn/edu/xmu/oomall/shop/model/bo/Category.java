package cn.edu.xmu.oomall.shop.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * 商品分类Bo
 * pid为0表示一级类，大于0表示二级类，为-1表示单独类
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/18
 */
@Data
@NoArgsConstructor
public class Category implements Serializable {
    private Long id;
    private String name;
    private Integer commissionRatio;
    private Long pid;
    private Long creatorId;
    private String creatorName;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtCreate;
    private Long modifierId;
    @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime gmtModified;
    private String modifierName;

}

