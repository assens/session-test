package com.foo.test.session;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private CookieProperties cookieProperties;

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  @ConditionalOnProperty(name = "server.servlet.session.session-id-resolver", havingValue = "header")
  public HttpSessionIdResolver headerHttpSessionIdResolver() {
    return new HeaderHttpSessionIdResolver("X-Session-Id");
  }

  @Bean
  @ConditionalOnProperty(name = "server.servlet.session.session-id-resolver", havingValue = "cookie")
  public CookieHttpSessionIdResolver cookieHttpSessionIdResolver() {
    final CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
    cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer());
    return cookieHttpSessionIdResolver;
  }

  @Bean
  @ConditionalOnProperty(name = "server.servlet.session.session-id-resolver", havingValue = "composite")
  public CompositeHttpSessionIdResolver compositeHttpSessionIdResolver() {
    return new CompositeHttpSessionIdResolver("X-Session-Id", cookieSerializer());
  }

  private DefaultCookieSerializer cookieSerializer() {
    final DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
    Optional.ofNullable(cookieProperties.getName()).ifPresent(cookieSerializer::setCookieName);
    Optional.ofNullable(cookieProperties.getMaxAge()).ifPresent(cookieMaxAge -> cookieSerializer.setCookieMaxAge((int) cookieMaxAge.toSeconds()));
    Optional.ofNullable(cookieProperties.getPath()).ifPresent(cookieSerializer::setCookiePath);
    Optional.ofNullable(cookieProperties.getDomain()).ifPresent(cookieSerializer::setDomainName);
    Optional.ofNullable(cookieProperties.getSecure()).ifPresent(cookieSerializer::setUseSecureCookie);
    cookieSerializer.setUseBase64Encoding(cookieProperties.getUseBase64Encoding());
    cookieSerializer.setUseHttpOnlyCookie(cookieProperties.getUseHttpOnlyCookie());
    return cookieSerializer;
  }

}
