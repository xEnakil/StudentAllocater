package com.university.StudentDropper.config;

import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class MockServiceConfig {

    @Bean
    public DataFactory dataFactory() {
        return new DataFactory();
    }

    @Bean
    public Random rand() {
        return new Random();
    }
}
