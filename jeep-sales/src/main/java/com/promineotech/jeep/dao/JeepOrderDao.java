package com.promineotech.jeep.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.promineotech.jeep.entity.Entities.Color;
import com.promineotech.jeep.entity.Entities.Customer;
import com.promineotech.jeep.entity.Entities.Engine;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Entities.Option;
import com.promineotech.jeep.entity.Entities.Order;
import com.promineotech.jeep.entity.Entities.Tire;

public interface JeepOrderDao {
  List<Option> fetchOptions(List<String> optionIds);
  
  Optional<Customer> fetchCustomer(String customerId);
  
  Optional<Jeep> fetchModel(JeepModel model, String trim, int doors);
  
  Optional<Color> fetchColor(String colorId);
  
  Optional<Engine> fetchEngine(String engineId);
  
  Optional<Tire> fetchTire(String tireId);

  /**
   * @param customer
   * @param jeep
   * @param color
   * @param engine
   * @param tire
   * @param price
   * @param options
   * @return
   */
  Order saveOrder(Customer customer, Jeep jeep, Color color, Engine engine, Tire tire,
      BigDecimal price, List<Option> options);
}