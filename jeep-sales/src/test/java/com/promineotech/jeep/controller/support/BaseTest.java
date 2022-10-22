/**
 * 
 */
package com.promineotech.jeep.controller.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import lombok.Getter;

/**
 * @author smith
 *
 */
public class BaseTest {
@LocalServerPort
private int serverPort;

@Getter
@Autowired
private TestRestTemplate restTemplate;

protected String getBaseUriForJeeps() {
  return String.format("http://localhost:%d/jeeps", serverPort);
}

protected String getBaseUriForOrders() {
  return String.format("http://localhost:%d/orders", serverPort);
}
}
