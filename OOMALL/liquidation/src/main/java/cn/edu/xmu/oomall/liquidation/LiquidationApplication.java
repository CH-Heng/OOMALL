package cn.edu.xmu.oomall.liquidation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wyg
 * @date 2021/11/30
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.*", "cn.edu.xmu.oomall.liquidation.*", "cn.edu.xmu.privilegegateway.*"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.liquidation.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.liquidation.microservice")
public class LiquidationApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiquidationApplication.class, args);
    }
}
