package com.school.store.admin.config.configurations;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfiguration {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("300MB"); // KB,MB
        factory.setMaxRequestSize("300MB");
        return factory.createMultipartConfig();
    }
}
