package org.repairflow.repairflowa.Exception.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

//    STATIC SUCCESS

    /**
     * Success , return data
     */

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "Operation successful",data);
    }

    /**
     * Success , return custom message and data
     */
    public static <T> ApiResponse<T> success( String message,T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, data);
    }


//      STATIC  FAIL
    /**
     * fail , return message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message, null);
    }

    /**
     * fail , return message
     */
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * fail, return message , code and data
     */
    public static <T> ApiResponse<T> error(String message, int errorCode, T data) {
        return new ApiResponse<>(errorCode, message, data);
    }
}
