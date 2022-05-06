package org.my.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonCfg {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer mapperBuilderCustomizer(){
        return builder -> builder.modulesToInstall(new JavaTimeModule());
    }
}
