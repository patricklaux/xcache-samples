package com.igeeksky.xcache.samples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Cache 注解示例
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.samples")
public class SpringAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAnnotationApplication.class, args);
    }

}