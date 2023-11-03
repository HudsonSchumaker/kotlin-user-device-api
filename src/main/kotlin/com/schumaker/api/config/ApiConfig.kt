package com.schumaker.api.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.modelmapper.config.Configuration.AccessLevel

@Configuration
class ApiConfig {

    @Bean
    fun getModelMapper(): ModelMapper {
        val mapper = ModelMapper()
        mapper.configuration
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(AccessLevel.PRIVATE)
        return mapper
    }
}