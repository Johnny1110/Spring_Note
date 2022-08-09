package com.example.finalversion.config;

import com.example.finalversion.mapper.CronMapper;
import com.example.finalversion.mapper.CronMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

    @Bean
    public CronMapper cronMapper(){
        return new CronMapperImpl();
    }

}
