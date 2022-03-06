package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.privilegegateway.annotation.aop.PageAspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


/**
 * @author Ming Qiu
 **/

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.goods","cn.edu.xmu.privilegegateway"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.goods.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.goods.microservice")
public class GoodsApplication {



    public static void main(String[] args) {

        SpringApplication.run(GoodsApplication.class, args);

    }

}

