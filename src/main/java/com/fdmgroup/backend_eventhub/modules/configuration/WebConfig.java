package com.fdmgroup.backend_eventhub.modules.configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private final String VIDEO_DIRECTORY_PATH = "file:src/main/resources/encoded/";
    private final String THUMBNAIL_DIRECTORY_PATH = "file:src/main/resources/thumbnails/";
    private final String POLL_OPTION_IMAGE_DIRECTORY_PATH = "file:src/main/resources/pollOptionImages/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/encoded/**")
                .addResourceLocations(VIDEO_DIRECTORY_PATH);
        registry.addResourceHandler("/thumbnails/**")
                .addResourceLocations(THUMBNAIL_DIRECTORY_PATH);
        registry.addResourceHandler("/pollOptionImages/**")
                .addResourceLocations(POLL_OPTION_IMAGE_DIRECTORY_PATH);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
        converters.add(new MappingJackson2HttpMessageConverter(
                new Jackson2ObjectMapperBuilder()
                        .dateFormat(new StdDateFormat())
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .build()));

    }
}
