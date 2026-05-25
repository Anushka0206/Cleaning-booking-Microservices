package com.booking.aiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "justlife.ai")
public class AiProperties {

  /** faq | openai */
  private String provider = "faq";

  private final OpenAi openai = new OpenAi();

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public OpenAi getOpenai() {
    return openai;
  }

  public static class OpenAi {
    private String apiKey = "";
    private String model = "gpt-4o-mini";
    private String baseUrl = "https://api.openai.com/v1";

    public String getApiKey() {
      return apiKey;
    }

    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }
  }
}
