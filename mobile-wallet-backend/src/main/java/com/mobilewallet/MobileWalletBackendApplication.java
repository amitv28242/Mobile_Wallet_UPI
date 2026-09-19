package com.mobilewallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.mobilewallet.repository")
@EntityScan(basePackages = "com.mobilewallet.entity")
public class MobileWalletBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileWalletBackendApplication.class, args);
    }
}