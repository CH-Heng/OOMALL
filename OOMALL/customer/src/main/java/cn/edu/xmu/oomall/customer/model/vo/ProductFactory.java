package cn.edu.xmu.oomall.customer.model.vo;

import cn.edu.xmu.oomall.customer.microservice.Vo.ProductVo;
import org.springframework.stereotype.Service;

@Service
public class ProductFactory {
    public ProductVo create(Long id) {
        ProductVo retVo = new ProductVo();

        retVo.setId(id);
        retVo.setPrice(100L);
        retVo.setName(String.format("货品-%d", id));
        retVo.setImageUrl(String.format("https://cn.bing.com/images/product-%d", id));

        return retVo;
    }
}
