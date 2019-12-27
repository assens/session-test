package com.foo.test.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired
  private SessionProperties sessionProperties;

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**").exposedHeaders(sessionProperties.getHeader());
  }
}
