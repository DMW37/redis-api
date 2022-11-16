package com.example.lock;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LockApplication {
    public static void main(String[] args) {
        SpringApplication.run(LockApplication.class, args);
    }

    @Bean
    public Redisson redisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://xjdmw:6379").setDatabase(0).setPassword("redis");
        return (Redisson)Redisson.create(config);
    }
}
