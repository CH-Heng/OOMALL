package cn.edu.xmu.oomall.share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.*","cn.edu.xmu.oomall.share.*", "cn.edu.xmu.privilegegateway.*"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.share.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.share.microservice")
public class ShareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareApplication.class, args);
    }
}
