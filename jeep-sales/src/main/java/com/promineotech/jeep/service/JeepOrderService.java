/**
 * 
 */
package com.promineotech.jeep.service;

import com.promineotech.jeep.entity.Entities.Order;
import com.promineotech.jeep.entity.Entities.OrderRequest;

/**
 * @author smith
 *
 */
public interface JeepOrderService {

  /**
   * @param orderRequest
   * @return
   */
  Order createOrder(OrderRequest orderRequest);

  
  
}
