package com.qikserve.grocerystore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "entity.path")
@Getter
@PropertySource("classpath:application.properties")
@Setter
public class EntityPaths {
    private String carts;
    private String discounts;
    private String items;
}
