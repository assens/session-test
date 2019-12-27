package com.foo.test.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

public class CompositeHttpSessionIdResolver implements HttpSessionIdResolver {

  private final HeaderHttpSessionIdResolver headerHttpSessionIdResolver;
  private final CookieHttpSessionIdResolver cookieHttpSessionIdResolver;

  public CompositeHttpSessionIdResolver(final String headerName, final CookieSerializer cookieSerializer) {
    this.headerHttpSessionIdResolver = new HeaderHttpSessionIdResolver(headerName);
    this.cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
    cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer);
  }

  @Override
  public List<String> resolveSessionIds(HttpServletRequest request) {
    final Set<String> sessionIds = new HashSet<>();
    sessionIds.addAll(headerHttpSessionIdResolver.resolveSessionIds(request));
    sessionIds.addAll(cookieHttpSessionIdResolver.resolveSessionIds(request));
    return new ArrayList<>(sessionIds);
  }

  @Override
  public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
    headerHttpSessionIdResolver.setSessionId(request, response, sessionId);
    cookieHttpSessionIdResolver.setSessionId(request, response, sessionId);
  }

  @Override
  public void expireSession(HttpServletRequest request, HttpServletResponse response) {
    headerHttpSessionIdResolver.expireSession(request, response);
    cookieHttpSessionIdResolver.expireSession(request, response);
  }

}
