package com.igeeksky.xcache.samples.annotation;

import com.igeeksky.xcache.aop.EnableCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Xcache 注解示例
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCache(basePackages = "com.igeeksky.xcache.samples")
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.samples")
public class AnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class, args);
    }

}