package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author jiangdong
 * @date 2021/8/12 21:48
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class LeyouGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouGoodsWebApplication.class,args);
    }
}
