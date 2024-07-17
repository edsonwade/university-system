### Refactored Service Method

````java
package com.example.marketplace.service;

import com.example.marketplace.exception.IllegalRequestException;
import com.example.marketplace.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Value("${app.customer.location.prefix}")
    private String customerLocationPrefix;

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer, HttpHeaders requestHeaders) {
        // Validate request headers
        validateRequestHeaders(requestHeaders);

        if (Objects.isNull(customer)) {
            logger.error("The 'customer' object must not be: {}", customer);
            throw new IllegalRequestException("The customer object must not be null.");
        }
        logger.info("Customer saved with success: {}", customer);

        // Save customer to repository
        Customer savedCustomer = customerRepository.save(customer);

        // Set response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(getCustomerLocationURI(savedCustomer.getCustomerId()));

        // Example of setting Cache-Control and ETag headers
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
````

## Refactored Controller Method

- **Adjust the CustomerController to reflect the changes**:

```java
  package com.example.marketplace.controller;

import com.example.marketplace.model.Customer;
import com.example.marketplace.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        Customer newCustomer = customerService.saveCustomer(customer, headers);
        return ResponseEntity
                .created(customerService.getCustomerLocationURI(newCustomer.getCustomerId()))
                .headers(customerService.getCustomerResponseHeaders(newCustomer.getCustomerId()))
                .body(newCustomer);
    }
}
```

### Customer Controller test methods

```java
package com.example.marketplace;

import com.example.marketplace.model.Customer;
import com.example.marketplace.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

        String customerJson =
                "{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"address\": \"123 Main St\" }";

        // When & Then
        mockMvc.perform(post("/api/customers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        header().string("Location", new URI("/api/customers/" + customer.getCustomerId()).toString()))
                .andExpect(header().string("ETag", is(customer.getCustomerId().toString())))
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.email", is(customer.getEmail())))
                .andExpect(jsonPath("$.address", is(customer.getAddress())));
    }
}
````

## Another example for customer controller test

```xml
<!-- Maven -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version> <!-- Replace with the latest version -->
</dependency>


```

- **Refactor Test to Use Jackson**: Instead of manually creating JSON strings, use Jackson’s ObjectMapper to serialize
  your Customer object to JSON:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

        // Serialize customer object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);

        // When & Then
        mockMvc.perform(post("/api/customers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        header().string("Location", new URI("/api/customers/" + customer.getCustomerId()).toString()))
                .andExpect(header().string("ETag", customer.getCustomerId().toString()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.address").value(customer.getAddress()));
    }
}

```

## Explanation

1. **ObjectMapper**: Jackson’s ObjectMapper is used to convert Java objects to JSON strings and vice versa. It handles
   serialization and deserialization of Java objects to JSON and provides flexibility in managing JSON structures.

2. **customerJson**: This variable holds the JSON string representation of the Customer object, ensuring that the test
   data is dynamically generated based on the Customer object’s properties.

3. **MockMvc**: Used for simulating HTTP requests to your controllers. Here, post("/api/customers/create") simulates a
   POST request to create a customer.

4. **Assertions**: The andExpect() methods verify the expected behavior of the controller’s response, such as HTTP
   status codes, headers, and JSON content.

## Test for Refactored Service Method

- **create a test that verifies the behavior of saveCustomer**: including header validation and response headers.

```java
package com.example.marketplace;

import com.example.marketplace.model.Customer;
import com.example.marketplace.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCustomerWithValidHeaders() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.set(HttpHeaders.ACCEPT, "application/json");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer, headers);

        verify(customerRepository, times(1)).save(customer);
        // Add assertions as needed
    }

    @Test
    public void testSaveCustomerWithInvalidContentTypeHeader() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
        headers.set(HttpHeaders.ACCEPT, "application/json");

        assertThrows(IllegalRequestException.class, () -> customerService.saveCustomer(customer, headers));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void testSaveCustomerWithInvalidAcceptHeader() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.set(HttpHeaders.ACCEPT, "text/plain");

        assertThrows(IllegalRequestException.class, () -> customerService.saveCustomer(customer, headers));

        verify(customerRepository, never()).save(any(Customer.class));
    }
}
```

## Request and Response Headers

### Request Headers

- **Content-Type**: Specifies the media type of the request body (e.g., `application/json`).
- **Accept**: Specifies the media types that are acceptable for the response (e.g., `application/json`).
- **Authorization**: Contains credentials for authenticating the client with the server.
- **Cache-Control**: Directs caching mechanisms on whether and how to cache the response.
- **Connection**: Specifies options for the current connection (e.g., `keep-alive`).

### Response Headers

- **Content-Type**: Specifies the media type of the response body (e.g., `application/json`).
- **Location**: Specifies a URI to redirect the client after a successful resource creation.
- **ETag**: A unique identifier for the version of the resource.
- **Cache-Control**: Directs how caching mechanisms should behave for the response.
- **Server**: Provides information about the server software handling the request.
- **Content-Length**: Indicates the size of the response body in bytes.

### Example

#### Request Example

```http
POST /api/customers/create HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "address": "123 Main St"
}
```

### Response example

```responses
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/customers/1
ETag: "1"
Cache-Control: no-cache

{
  "customerId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "address": "123 Main St"
}
```

- **Integrate Accept-Language and optionally Cache-Control validation into your validateRequestHeaders method**:

```java
private void validateRequestHeaders(HttpHeaders headers) {
    String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
    String accept = headers.getFirst(HttpHeaders.ACCEPT);
    String acceptLanguage = headers.getFirst(HttpHeaders.ACCEPT_LANGUAGE);
    String cacheControl = headers.getFirst(HttpHeaders.CACHE_CONTROL);

    if (!"application/json".equals(contentType)) {
        logger.error("Invalid Content-Type header: {}", contentType);
        throw new IllegalRequestException("Content-Type must be application/json");
    }

    if (!"application/json".equals(accept)) {
        logger.error("Invalid Accept header: {}", accept);
        throw new IllegalRequestException("Accept must be application/json");
    }

    // Example: Validate Accept-Language header
    if (!"en".equals(acceptLanguage)) {
        logger.error("Invalid Accept-Language header: {}", acceptLanguage);
        throw new IllegalRequestException("Accept-Language must be 'en' for English");
    }

    // Example: Validate Cache-Control header (if needed)
    if (!"no-cache".equals(cacheControl)) {
        logger.warn("Unexpected Cache-Control header: {}", cacheControl);
        // Optionally, handle unexpected cache control directives
    }
}
```

### Best Practices:

- **Validate Headers Early**: Validate headers as early as possible in your service flow to detect issues before
  proceeding with business logic.

- **Use Error Responses**: Use appropriate HTTP status codes (e.g., 400 for bad requests) and error messages when
  headers are invalid.

- **Document Header Requirements**: Clearly document which headers are required or accepted by your API endpoints.

- **Unit Test Header Validation**: Write unit tests to verify that your header validation logic behaves correctly under
  different scenarios.

## An example of Unit test for CustomerServiceImpl

``` java
import com.example.marketplace.exception.IllegalRequestException;
import com.example.marketplace.model.Customer;
import com.example.marketplace.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private HttpHeaders requestHeaders;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        // Initialize a sample customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St, Anytown");

        // Mock behavior for HttpHeaders
        when(requestHeaders.getFirst(HttpHeaders.CONTENT_TYPE)).thenReturn("application/json");
        when(requestHeaders.getFirst(HttpHeaders.ACCEPT)).thenReturn("application/json");
    }

    @Test
    void saveCustomer_ValidCustomer_Success() {
        // Mock behavior for customerRepository.save()
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Call the saveCustomer method
        Customer savedCustomer = customerService.saveCustomer(customer, requestHeaders);

        // Verify that customerRepository.save() was called once with the correct argument
        verify(customerRepository, times(1)).save(customer);

        // Validate the saved customer
        assertNotNull(savedCustomer);
        assertEquals(customer.getId(), savedCustomer.getId());
        assertEquals(customer.getName(), savedCustomer.getName());
        assertEquals(customer.getEmail(), savedCustomer.getEmail());
        assertEquals(customer.getAddress(), savedCustomer.getAddress());
    }

    @Test
    void saveCustomer_NullCustomer_ExceptionThrown() {
        // Call saveCustomer with null customer
        assertThrows(IllegalRequestException.class, () -> customerService.saveCustomer(null, requestHeaders));

        // Verify that customerRepository.save() was not called
        verify(customerRepository, never()).save(any());

        // Ensure no other interactions with customerRepository
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void saveCustomer_InvalidContentType_ExceptionThrown() {
        // Mock behavior for HttpHeaders with invalid Content-Type
        when(requestHeaders.getFirst(HttpHeaders.CONTENT_TYPE)).thenReturn("application/xml");

        // Call saveCustomer with valid customer but invalid Content-Type
        assertThrows(IllegalRequestException.class, () -> customerService.saveCustomer(customer, requestHeaders));

        // Verify that customerRepository.save() was not called
        verify(customerRepository, never()).save(any());

        // Ensure no other interactions with customerRepository
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void saveCustomer_InvalidAcceptHeader_ExceptionThrown() {
        // Mock behavior for HttpHeaders with invalid Accept header
        when(requestHeaders.getFirst(HttpHeaders.ACCEPT)).thenReturn("application/xml");

        // Call saveCustomer with valid customer but invalid Accept header
        assertThrows(IllegalRequestException.class, () -> customerService.saveCustomer(customer, requestHeaders));

        // Verify that customerRepository.save() was not called
        verify(customerRepository, never()).save(any());

        // Ensure no other interactions with customerRepository
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void saveCustomer_ExceptionInCustomerRepository_ExceptionThrown() {
        // Mock behavior for customerRepository.save() to throw an exception
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Database error"));

        // Call saveCustomer with valid customer
        assertThrows(RuntimeException.class, () -> customerService.saveCustomer(customer, requestHeaders));

        // Verify that customerRepository.save() was called once with the correct argument
        verify(customerRepository, times(1)).save(customer);

        // Ensure no other interactions with customerRepository
        verifyNoMoreInteractions(customerRepository);
    }
}
```

## Explanation

1. **Setup (@BeforeEach)**: This method initializes a sample Customer object and mocks the behavior of HttpHeaders to
   return valid values for CONTENT_TYPE and ACCEPT headers.

2. **saveCustomer_ValidCustomer_Success**: Tests the scenario where a valid Customer object is passed to saveCustomer,
   ensuring it's saved correctly and returned with the expected values.

3. **saveCustomer_NullCustomer_ExceptionThrown**: Tests the scenario where null is passed as the Customer object to
   saveCustomer, expecting an IllegalRequestException to be thrown.

4. **saveCustomer_InvalidContentType_ExceptionThrown**: Tests the scenario where an invalid Content-Type header
   (application/xml) is passed in the request headers, expecting an IllegalRequestException to be thrown.

5. **saveCustomer_InvalidAcceptHeader_ExceptionThrown**: Tests the scenario where an invalid Accept header
   (application/xml) is passed in the request headers, expecting an IllegalRequestException to be thrown.

6. **saveCustomer_ExceptionInCustomerRepository_ExceptionThrown**: Tests the scenario where an exception is thrown by
   customerRepository.save() method, verifying that the exception propagates correctly.