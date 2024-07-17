###  Logback (SLF4J) for Logging

Spring Boot uses SLF4J (Simple Logging Facade for Java) for logging abstraction and Logback as the default logging implementation. You can customize Logback's configuration to direct logs to a file.

1. **Add Logback Configuration File**: Create a logback-spring.xml file under the src/main/resources directory of your 
   project. This file will override the default logging configuration provided by Spring Boot.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Appenders -->
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_PATH:-logs}/application.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root logger level and appender -->
    <root level="info">
        <appender-ref ref="FILE" />
    </root>

</configuration>
```
- **Explanation**:
  1. file: Specify the path and name of the log file. Here, ${LOG_PATH:-logs}/application.log means logs will be 
  stored in a logs directory relative to your project root. You can change the application.log to any desired filename.
  2. pattern: Defines the log message format. Customize this pattern based on your preferences.

2. **Configure Logging Levels**: By default, logs are written to the console (System.out) and can be redirected to a file as shown above. Adjust <root level="info"> to set the logging level (trace, debug, info, warn, error) based on your needs.

## Ensure Logging in Your Application
- Modify your CustomerServiceImpl and CustomerController classes to use SLF4J logging:
```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Value("${app.customer.location.prefix}")
    private String customerLocationPrefix;

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer, HttpHeaders requestHeaders) {
        // Validate request headers
        validateRequestHeaders(requestHeaders);

        if (Objects.isNull(customer)) {
            logger.error("The 'customer' object must not be null.");
            throw new IllegalRequestException("The customer object must not be null.");
        }
        logger.info("Customer saved successfully: {}", customer);

        // Save customer to repository
        Customer savedCustomer = customerRepository.save(customer);

        // Set response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(getCustomerLocationURI(savedCustomer.getCustomerId()));
        responseHeaders.setCacheControl("no-cache");
        responseHeaders.setETag(savedCustomer.getCustomerId().toString());

        return savedCustomer;
    }

    private void validateRequestHeaders(HttpHeaders headers) {
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        String accept = headers.getFirst(HttpHeaders.ACCEPT);

        if (!"application/json".equals(contentType)) {
            logger.error("Invalid Content-Type header: {}", contentType);
            throw new IllegalRequestException("Content-Type must be application/json");
        }

        if (!"application/json".equals(accept)) {
            logger.error("Invalid Accept header: {}", accept);
            throw new IllegalRequestException("Accept must be application/json");
        }
    }

    private URI getCustomerLocationURI(Long customerId) {
        try {
            return new URI(customerLocationPrefix + customerId);
        } catch (URISyntaxException e) {
            logger.error("Error creating customer location URI: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid URI syntax", e);
        }
    }
}

    @Value("${app.customer.location.prefix}")
    private String customerLocationPrefix;

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer, HttpHeaders requestHeaders) {
        // Validate request headers
        validateRequestHeaders(requestHeaders);

        if (Objects.isNull(customer)) {
            logger.error("The 'customer' object must not be null.");
            throw new IllegalRequestException("The customer object must not be null.");
        }
        logger.info("Customer saved successfully: {}", customer);

        // Save customer to repository
        Customer savedCustomer = customerRepository.save(customer);

        // Set response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(getCustomerLocationURI(savedCustomer.getCustomerId()));
        responseHeaders.setCacheControl("no-cache");
        responseHeaders.setETag(savedCustomer.getCustomerId().toString());

        return savedCustomer;
    }

    private void validateRequestHeaders(HttpHeaders headers) {
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        String accept = headers.getFirst(HttpHeaders.ACCEPT);

        if (!"application/json".equals(contentType)) {
            logger.error("Invalid Content-Type header: {}", contentType);
            throw new IllegalRequestException("Content-Type must be application/json");
        }

        if (!"application/json".equals(accept)) {
            logger.error("Invalid Accept header: {}", accept);
            throw new IllegalRequestException("Accept must be application/json");
        }
    }

    private URI getCustomerLocationURI(Long customerId) {
        try {
            return new URI(customerLocationPrefix + customerId);
        } catch (URISyntaxException e) {
            logger.error("Error creating customer location URI: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid URI syntax", e);
        }
    }
}
```
- **CustomerController**
```java
import com.example.marketplace.model.Customer;
import com.example.marketplace.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody Customer customer) {

        try {
            Customer newCustomer = customerService.saveCustomer(customer, headers);
            return ResponseEntity
                    .created(customerService.getCustomerLocationURI(newCustomer.getCustomerId()))
                    .headers(customerService.getCustomerResponseHeaders(newCustomer.getCustomerId()))
                    .body(newCustomer);
        } catch (Exception e) {
            logger.error("Error creating customer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```