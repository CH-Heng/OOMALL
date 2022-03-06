package cn.edu.xmu.oomall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author RenJieZheng 22920192204334
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.order","cn.edu.xmu.privilegegateway"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.order.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.order.microservice")
@EnableDiscoveryClient
public class OrderApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(OrderApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

