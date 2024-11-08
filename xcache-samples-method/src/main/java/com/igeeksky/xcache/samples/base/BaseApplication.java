package com.igeeksky.xcache.samples.base;

import com.igeeksky.xcache.samples.UserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@Import(UserDao.class)
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache.samples")
public class BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }

}