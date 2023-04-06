package com.shopproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}")// application.properties 설정한 uploadPath값을 읽어옵니다.
    String uploadPath;
    //uploadPath = "C:/shopproject
    // images/item/xxx.jpg
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);//로컬 컴퓨터에 저장된 파일을 읽어올 root 경로를 설정합니다.
    }
}
