package cn.edu.xmu.oomall.coupon.microservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductVo implements Serializable {

    private Long id;

    private String name;

    private String imageUrl;

}
