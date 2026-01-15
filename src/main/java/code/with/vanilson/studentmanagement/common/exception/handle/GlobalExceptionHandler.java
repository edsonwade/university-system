package code.with.vanilson.studentmanagement.common.exception.handle;

import code.with.vanilson.studentmanagement.common.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.Objects;

import static code.with.vanilson.studentmanagement.common.constants.Constant.*;
import static code.with.vanilson.studentmanagement.common.constants.ErrorCode.*;
import static java.time.LocalDateTime.now;

/**
 * GlobalExceptionHandler
 *
 * @author vamuhong
 * @version 1.1
 * @since 2024-07-05
 */
@RestControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("all")
public class GlobalExceptionHandler {

        private final MessageSource messageSource;

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                return buildErrorResponse(ex, NOT_FOUND, ZONE_LISBON, HttpStatus.NOT_FOUND, request);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex,
                        HttpServletRequest request) {
                var errorResponse = new ErrorResponse(
                                ex.getMessage(),
                                NOT_FOUND,
                                ZONE_LISBON,
                                getPath(request),
                                Objects.requireNonNull(HttpStatus.NOT_FOUND).value(),
                                now());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ResourceBadRequestException.class)
        public ResponseEntity<ErrorResponse> handleResourceBadRequestException(ResourceBadRequestException ex,
                        HttpServletRequest request) {
                return buildErrorResponse(ex, BAD_REQUEST, ZONE_LONDON, HttpStatus.BAD_REQUEST, request);
        }

        @ExceptionHandler(ResourceAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
                        HttpServletRequest request) {
                return buildErrorResponse(ex, CONFLICT, ZONE_PARIS, HttpStatus.CONFLICT, request);
        }

        @ExceptionHandler(ResourceValidationException.class)
        public ResponseEntity<ErrorResponse> handleResourceValidationException(ResourceValidationException ex,
                        HttpServletRequest request) {
                return buildErrorResponse(ex, UNPROCESSABLE_ENTITY, ZONE_HONG_KONG, HttpStatus.UNPROCESSABLE_ENTITY,
                                request);
        }

        @ExceptionHandler(InternalServerErrorException.class)
        public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                getMessage("error.internal_server_error", null),
                                INTERNAL_SERVER_ERROR,
                                ZONE_HONG_KONG,
                                getPath(request),
                                Objects.requireNonNull(HttpStatus.INTERNAL_SERVER_ERROR).value(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                ex.getMessage(),
                                INTERNAL_SERVER_ERROR,
                                ZONE_HONG_KONG,
                                getPath(request),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        private ResponseEntity<ErrorResponse> buildErrorResponse(BaseException ex, String errorCode, String zone,
                        HttpStatus status, HttpServletRequest request) {
                String message = getMessage(ex.getMessage(), ex.getArgs());
                var errorResponse = new ErrorResponse(
                                message,
                                errorCode,
                                zone,
                                getPath(request),
                                status.value(),
                                now());
                return new ResponseEntity<>(errorResponse, status);
        }

        private String getMessage(String key, Object[] args) {
                try {
                        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
                } catch (Exception e) {
                        return key;
                }
        }

        private String getPath(HttpServletRequest request) {
                return new ServletWebRequest(request)
                                .getRequest()
                                .getRequestURI();
        }
}