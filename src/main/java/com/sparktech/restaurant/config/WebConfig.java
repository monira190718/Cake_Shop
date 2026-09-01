package com.sparktech.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig
        implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        //cakepicture

        Path cakeDirectory = Paths
                .get("uploads", "cakes")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/cake-images/**")
                .addResourceLocations(
                        cakeDirectory.toUri().toString()
                );

        Path uploadDirectory =
                Paths.get(
                        "uploads",
                        "profile-pictures"
                ).toAbsolutePath().normalize();

        String uploadLocation =
                uploadDirectory
                        .toUri()
                        .toString();

        registry.addResourceHandler(
                        "/profile-images/**"
                )
                .addResourceLocations(
                        uploadLocation
                );
        // customize cake images

        Path customizeDirectory =
                Paths.get(
                                "uploads",
                                "customize"
                        )
                        .toAbsolutePath()
                        .normalize();


        registry.addResourceHandler(
                        "/customize-images/**"
                )
                .addResourceLocations(
                        customizeDirectory.toUri().toString()
                );
    }
}