package com.shopacc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.shopacc.repository")
public class JpaConfig {
    // Optionally configure physical naming strategy via properties (we use application.yml)
}
