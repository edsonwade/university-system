### Creating the messages.properties File

-**Create a file named messages.properties**: in the src/main/resources directory of your project.

### example

```properties
# Student messages
student.not_found=Student with ID {0} not found.
student.already_exists=Student with ID {0} already exists.
student.invalid_data=Invalid data for student with ID {0}.
# Degree messages
degree.not_found=Degree with ID {0} not found.
degree.already_exists=Degree with ID {0} already exists.
degree.invalid_data=Invalid data for degree with ID {0}.
# Course messages
course.not_found=Course with ID {0} not found.
course.already_exists=Course with ID {0} already exists.
course.invalid_data=Invalid data for course with ID {0}.
# Teacher messages
teacher.not_found=Teacher with ID {0} not found.
teacher.already_exists=Teacher with ID {0} already exists.
teacher.invalid_data=Invalid data for teacher with ID {0}.
# Appointment messages
appointment.not_found=Appointment with ID {0} not found.
appointment.already_exists=Appointment with ID {0} already exists.
appointment.invalid_data=Invalid data for appointment with ID {0}.
# Availability messages
availability.not_found=Availability with ID {0} not found.
availability.already_exists=Availability with ID {0} already exists.
availability.invalid_data=Invalid data for availability with ID {0}.
```

### Reading Messages from messages.properties

You need to create a utility class to read messages from the messages.properties file.

## MessageSourceConfig

1. configure MessageSource in a configuration class:

```java
package com.example.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
```

2. MessageUtil
   Next, create a utility class to fetch messages from the messages.properties file:

```java
package com.example.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {
    private final MessageSource messageSource;

    @Autowired
    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}
```

3. Using MessageUtil in Exception Handling
   Update your exception handling classes to use the messages from the messages.properties file.
```java
package com.example.studentmanagement.services;

import com.example.common.exceptions.EntityNotFoundException;
import com.example.common.utils.MessageUtil;
import com.example.studentmanagement.entities.Student;
import com.example.studentmanagement.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final MessageUtil messageUtil;

    @Autowired
    public StudentService(StudentRepository studentRepository, MessageUtil messageUtil) {
        this.studentRepository = studentRepository;
        this.messageUtil = messageUtil;
    }

    public Student findStudentById(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isEmpty()) {
            throw new EntityNotFoundException(messageUtil.getMessage("student.not_found", studentId));
        }
        return student.get();
    }

    // Other service methods
}
```
4. Global Exception Handler Update
```java
package com.example.common.exceptions;

import com.example.common.constants.ErrorCodes;
import com.example.common.responses.ErrorResponse;
import com.example.common.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    @Autowired
    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleEntityBadRequestException(EntityBadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.CONFLICT,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ErrorResponse> handleEntityValidationException(EntityValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.UNPROCESSABLE_ENTITY,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.SERVICE_UNAVAILABLE,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ErrorCodes.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                messageUtil.getMessage(ErrorCodes.UNEXPECTED_ERROR),
                ErrorCodes.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
### Summary
By using the messages.properties file and the MessageUtil class, you can easily manage and retrieve messages for 
different entities and exceptions.
This approach helps you keep the messages centralized, making it easier to maintain and update them as needed.