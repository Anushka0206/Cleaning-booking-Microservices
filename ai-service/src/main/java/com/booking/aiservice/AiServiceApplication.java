package com.booking.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.booking.aiservice.config.AiProperties;
import com.booking.aiservice.config.DotenvLoader;

@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class AiServiceApplication {

  public static void main(String[] args) {
    // Loads ai-service/.env and project-root/.env (see ai-service/.env.example)
    DotenvLoader.load();
    SpringApplication.run(AiServiceApplication.class, args);
  }
}
