package com.foo.test.session;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private CookieProperties cookieProperties;

  @Autowired
  private SessionProperties sessionProperties;

  @Autowired
  private ObjectMapper objectMapper;

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  @ConditionalOnProperty(name = "server.servlet.session.session-id-resolver", havingValue = "header")
  public HttpSessionIdResolver headerHttpSessionIdResolver() {
    return new HeaderHttpSessionIdResolver(sessionProperties.getHeader());
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
    return new CompositeHttpSessionIdResolver(sessionProperties.getHeader(), cookieSerializer());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin().defaultSuccessUrl("/actuator/health")
        .successHandler(successHandler())
        .and().httpBasic()
        .and().cors()
        .and().csrf().disable();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of(sessionProperties.getHeader()));
    configuration.setAllowCredentials(true);
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private AuthenticationSuccessHandler successHandler() {
    return (request, response, authentication) -> {
      log.info("[{}][logged in]", authentication.getName());
      response.setCharacterEncoding(Charset.defaultCharset().toString());
      response.setStatus(HttpStatus.OK_200);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), Map.of(sessionProperties.getHeader(), request.getSession().getId()));
      response.getWriter().flush();
      response.getWriter().close();
    };
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
