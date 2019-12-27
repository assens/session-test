package com.foo.test.session;

import java.time.Instant;

import javax.servlet.http.HttpSession;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpSessionEventListener {

  @EventListener(HttpSessionCreatedEvent.class)
  public void onHttpSessionCreatedEvent(final HttpSessionCreatedEvent event) {
    final HttpSession session = event.getSession();
    log.info("[{}][CREATED]", session.getId());
  }

  @EventListener(HttpSessionDestroyedEvent.class)
  public void onHttpSessionDestroyedEvent(final HttpSessionDestroyedEvent event) {
    final HttpSession session = event.getSession();
    event.getSecurityContexts().forEach(securityContext -> {
      final Authentication authentication = securityContext.getAuthentication();
      log.info("[{}][Logged out]", authentication.getName());
    });
    log.info("[{}][DESTROYED][lastAccessedTime: {}]", session.getId(), Instant.ofEpochMilli(session.getLastAccessedTime()));
  }
}
