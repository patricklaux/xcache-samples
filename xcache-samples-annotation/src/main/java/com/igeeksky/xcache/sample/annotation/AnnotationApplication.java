package com.igeeksky.xcache.sample.annotation;

import com.igeeksky.xcache.aop.EnableCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCache(basePackages = "com.igeeksky.xcache.sample.annotation")
@SpringBootApplication
public class AnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class, args);
    }

}