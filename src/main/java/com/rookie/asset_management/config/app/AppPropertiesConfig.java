package com.rookie.asset_management.config.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppPropertiesConfig {
  private static String uiUrl;

  @Value("${com.rookie.asset_management.ui.url}")
  public void setUiUrl(String url) {
    AppPropertiesConfig.uiUrl = url;
  }

  public static String getUiUrl() {
    return uiUrl;
  }
}
