package com.igeeksky.xcache.sample.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.sample.spring")
public class SpringAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAnnotationApplication.class, args);
    }

}