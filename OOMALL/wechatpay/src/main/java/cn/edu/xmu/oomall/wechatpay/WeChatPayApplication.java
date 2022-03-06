package cn.edu.xmu.oomall.wechatpay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.model","cn.edu.xmu.oomall.core.util", "cn.edu.xmu.oomall.wechatpay", "cn.edu.xmu.privilegegateway"})@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.wechatpay.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.wechatpay.microservice")
public class WeChatPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeChatPayApplication.class, args);
    }

}
