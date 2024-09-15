package com.igeeksky.xcache.samples.spring;

import com.igeeksky.xcache.samples.UserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@EnableCaching
@Import(UserDao.class)
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.samples")
public class SpringAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAnnotationApplication.class, args);
    }

}