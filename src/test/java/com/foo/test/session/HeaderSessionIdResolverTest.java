package com.foo.test.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = {"server.servlet.session.session-id-resolver=header"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Slf4j
class HeaderSessionIdResolverTest {

  @Autowired
  private TestRestTemplate template;

  @Test
  void testHeaderSessionIdResolver() {

    // act

    final ResponseEntity<String> response = template
        .withBasicAuth("admin", "admin")
        .getForEntity("/actuator/health", String.class);
    log.info(response.toString());

    // assert

    final HttpHeaders headers = response.getHeaders();
    assertEquals(OK, response.getStatusCode());
    assertTrue(headers.containsKey("X-Session-Id"));
    assertTrue(!headers.containsKey("Set-Cookie"));
  }

}
