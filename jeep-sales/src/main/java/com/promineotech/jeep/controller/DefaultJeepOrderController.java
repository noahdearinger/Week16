/**
 * 
 */
package com.promineotech.jeep.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.promineotech.jeep.entity.Entities.Order;
import com.promineotech.jeep.entity.Entities.OrderRequest;
import com.promineotech.jeep.service.JeepOrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author smith
 *
 */
@RestController
@Slf4j
public class DefaultJeepOrderController implements JeepOrderController {

  @Autowired
 private JeepOrderService jeepOrderService;
  
  @Transactional
  @Override
  public Order CreateOrder(OrderRequest orderRequest) {
  log.debug("Order= {}", orderRequest);
  return jeepOrderService.createOrder(orderRequest);
  }

}
