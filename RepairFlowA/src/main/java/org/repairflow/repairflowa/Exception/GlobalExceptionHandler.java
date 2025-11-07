package org.repairflow.repairflowa.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse<Object> handleResourceNotFoundException(AppExceptions.ResourceNotFoundException ex){
        return ApiResponse.error(ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ResponseBody
    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.error("Validation failed", 400, errors);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ResponseBody
    public ApiResponse<Object> handleGenericException(Exception ex) {

         logger.error("An unexpected error occurred: ", ex);


        return ApiResponse.error("An internal server error occurred", 500);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Object> handleBadCredException(BadCredentialsException ex) {
        return ApiResponse.error("Invalid email or password", 401);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ApiResponse.error("Username not found", 404);
    }

}
