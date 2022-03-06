package cn.edu.xmu.oomall.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wwk
 * @date 2021/11/30
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.*", "cn.edu.xmu.oomall.customer.*", "cn.edu.xmu.privilegegateway.*"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.customer.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.customer.microservice")
public class CustomerApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CustomerApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


