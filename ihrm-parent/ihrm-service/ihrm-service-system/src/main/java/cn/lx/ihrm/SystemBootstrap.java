package cn.lx.ihrm;

import cn.lx.ihrm.common.entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * cn.lx.ihrm
 *
 * @Author Administrator
 * @date 15:07
 */
@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = "cn.lx.ihrm.common.domain.system")
@EnableFeignClients(basePackages = {"cn.lx.ihrm.common.feign"})
public class SystemBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SystemBootstrap.class, args);
    }

    @Bean
    public IdWorker idWorkker() {
        return new IdWorker(1, 1);
    }
}
