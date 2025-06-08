package com.tranqilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This makes files in the 'user-uploads' directory accessible via the URL '/user-uploads/**'
        // For example, an image at 'user-uploads/profile-pictures/my-image.png'
        // will be available at 'http://localhost:8080/user-uploads/profile-pictures/my-image.png'
        registry.addResourceHandler("/user-uploads/**")
                .addResourceLocations("file:user-uploads/");
    }
}