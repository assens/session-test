package com.foo.test.session;

import static com.foo.test.session.SessionProperties.SessionIdResolver.COOKIE;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "server.servlet.session")
public class SessionProperties extends Session {

  public enum SessionIdResolver {
    HEADER, COOKIE, COMPOSITE
  }

  @Setter
  @Getter
  private SessionIdResolver sessionIdResolver = COOKIE;

  @Getter
  @Setter
  private String header = "X-Session-Id";
}
