package com.igeeksky.xcache.samples.annotation;

import com.igeeksky.xcache.aop.EnableCache;
import com.igeeksky.xcache.samples.UserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCache(basePackages = "com.igeeksky.xcache.samples")
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.samples")
@Import({UserDao.class})
public class AnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class, args);
    }

}