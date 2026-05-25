package com.booking.aiservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.booking.aiservice.service.provider.AiProvider;
import com.booking.aiservice.service.provider.FaqAiProvider;
import com.booking.aiservice.service.provider.OpenAiProvider;

@Configuration
public class AiProviderConfig {

  @Bean
  public FaqAiProvider faqAiProvider() {
    return new FaqAiProvider();
  }

  @Bean
  public OpenAiProvider openAiProvider(AiProperties properties, RestClient restClient) {
    return new OpenAiProvider(properties, restClient);
  }

  @Bean
  public AiProvider activeAiProvider(AiProperties properties, FaqAiProvider faq, OpenAiProvider openai) {
    return "openai".equals(AiEnvHelper.providerMode(properties)) ? openai : faq;
  }
}
