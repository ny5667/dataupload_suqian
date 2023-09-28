package com.supcon.ses.dataupload.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supcon.ses.datauploadparent.repository.TagRestTemplateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration {

    @Bean
    public TagRestTemplateRepository tagRestTemplateRepository(ObjectMapper objectMapper) {
        return new TagRestTemplateRepository(objectMapper);
    }

}