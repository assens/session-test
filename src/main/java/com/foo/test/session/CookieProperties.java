package com.foo.test.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.server.Session.Cookie;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieProperties extends Cookie {
  @Getter
  @Setter
  private Boolean useBase64Encoding = true;

  @Getter
  @Setter
  private Boolean useHttpOnlyCookie = true;
}
