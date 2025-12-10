package org.repairflow.repairflowa.Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return new ResponseEntity<>(ApiResponse.error(errorCode.getMessage(),errorCode.getCode()),errorCode.getStatus());
    }


    @ExceptionHandler({AuthorizationDeniedException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception ex){
        return new ResponseEntity<>(ApiResponse.error("Access denied",HttpStatus.FORBIDDEN.value()),HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));

        return new ResponseEntity<>(ApiResponse.error(message,HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception ex){
        logger.error("Exception caught in GlobalExceptionHandler", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value()),HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ResponseBody
//    public ApiResponse<Object> handleResourceNotFoundException(AppExceptions.ResourceNotFoundException ex){
//        return ApiResponse.error(ex.getMessage());
//    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
//    @ResponseBody
//    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        return ApiResponse.error("Validation failed", 400, errors);
//    }




//
//    @ExceptionHandler(BadCredentialsException.class)
//    public ApiResponse<Object> handleBadCredException(BadCredentialsException ex) {
//        return ApiResponse.error("Invalid email or password", 401);
//    }
//
//
//    @ExceptionHandler(UsernameNotFoundException.class)
//    public ApiResponse<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
//        return ApiResponse.error("Username not found", 404);
//    }

//    @ExceptionHandler(AuthenticationException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ResponseBody
//    public ApiResponse<Object> handleAuthenticationException(AuthenticationException ex) {
//        logger.error("Authentication failed for user: {}", ex.getMessage());
//        return ApiResponse.error("Invalid username or password", 401);
//    }

//    @ExceptionHandler(AuthorizationDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ResponseBody
//    public ApiResponse<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
//        logger.error("Authorization failed for user: {}", ex.getMessage());
//        return ApiResponse.error("You do not have permission to access this resource", 403);
//    }


//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
//    @ResponseBody
//    public ApiResponse<Object> handleGenericException(Exception ex) {
//
//        logger.error("An unexpected error occurred: ", ex);
//
//
//        return ApiResponse.error("An internal server error occurred", 500);
//    }
}
