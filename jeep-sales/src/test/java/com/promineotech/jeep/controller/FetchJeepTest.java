/**
 * 
 */
package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.JeepSales;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;

/**
 * @author smith
 *
 */

class FetchJeepTest {
  
  
  @Nested
//@formatter:off
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {
   "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
   "classpath:flyway/migrations/V1.1__Jeep_Data.sql"
   },
config = @SqlConfig(encoding = "utf-8"))
//@formatter:on
  class TestsThatDoNotPolluteApplicationContext extends FetchJeepTestSupport{
    @Test
    void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
   //Given: a valid model, trim, and URI
      JeepModel model = JeepModel.WRANGLER;
     String trim = "Sport";
  // @formatter:off
     String uri = String.format("%s?model=%s&trim=%s", 
         getBaseUriForJeeps(), model, trim);
     
     //When: A connection is made to the URI
     //http request to rest service
     ResponseEntity<List<Jeep>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
         null, new ParameterizedTypeReference<List<Jeep>>() {});
  // @formatter:on
     
     //Then: A success (OK - 200) status code is returned
     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     
     //And: The actual list returned is the same as the expected list
     List<Jeep> actual = response.getBody();
     List<Jeep> expected = buildExpected();
     
     assertThat(actual).isEqualTo(expected);
    }
   
    
    
    @Test
    void testThatAnErrorMessageIsReturnedWhenAnUnknownTrimIsSupplied() {
    
      //Given: an invalid trim level
      JeepModel model = JeepModel.WRANGLER;
     String trim = "Unknown value";
  // @formatter:off
     String uri = String.format("%s?model=%s&trim=%s", 
         getBaseUriForJeeps(), model, trim);
     
     // When: a connection is made to URI
     //http request to rest service
     ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
         null, new ParameterizedTypeReference<>() {});
  // @formatter:on
 
     // Then: a not found (404) action is returned
     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
     
     // And: an error message is returned
   Map<String, Object> error = response.getBody();
   
   assertErrorMessageValid(error, HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @MethodSource("com.promineotech.jeep.controller.FetchJeepTest#parametersForInvalidInput")
    void testThatAnErrorMessageIsReturnedWhenAnInvalidValueIsSupplied(
        String model, String trim, String reason) {
    
      //Given: an invalid trim level
   
  // @formatter:off
     String uri = String.format("%s?model=%s&trim=%s", 
       getBaseUriForJeeps(), model, trim);
     
     // When: a connection is made to URI
     //http request to rest service
     ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
         null, new ParameterizedTypeReference<>() {});
  // @formatter:on
     
     // Then: a not found (404) action is returned
     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
     
     // And: an error message is returned
   Map<String, Object> error = response.getBody();
   
   assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);
    }
   
  }
  
  static Stream<Arguments> parametersForInvalidInput() {
    return Stream.of(
          arguments("WRANGLER", "@#$*", "Trim contains non aplha-numeric characters."),
          arguments("WRANGLER", "C".repeat(Constants.TRIM_MAX_LENGTH + 1), "Trim character length too long."),
          arguments("INVALID", "Sport", "model not in enum.")
          
        );
        
  }
  
  @Nested
//@formatter:off
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {JeepSales.class})
@ActiveProfiles("test")
@Sql(scripts = {
   "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
   "classpath:flyway/migrations/V1.1__Jeep_Data.sql"
   },
config = @SqlConfig(encoding = "utf-8"))
//@formatter:on
  class TestsThatPolluteApplicationContext extends FetchJeepTestSupport {
   
    @MockBean
    private JeepSalesService jeepSalesService;
    
    
    @Test
    void testThatAnUnplannedErrorResultsInA500Status() {
    
      //Given: an invalid trim level
      JeepModel model = JeepModel.WRANGLER;
     String trim = "invalid";
  // @formatter:off
     String uri = String.format("%s?model=%s&trim=%s", 
         getBaseUriForJeeps(), model, trim);
     
     doThrow(new RuntimeException("Ouch")).when(jeepSalesService).fetchJeeps(model, trim);
     
     // When: a connection is made to URI
     //http request to rest service
     ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
         null, new ParameterizedTypeReference<>() {});
  // @formatter:on
 
     // Then: an Internal Server Error is returned
     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
     
     // And: an error message is returned
   Map<String, Object> error = response.getBody();
   
   assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  

  
}
