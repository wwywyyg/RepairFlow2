package org.repairflow.repairflowa.Exception;

import org.springframework.http.HttpStatus;

/**
 * @author guangyang
 * @date 12/1/25 PM7:24
 * @description TODO: Description
 */
public class BaseAppException extends RuntimeException {
    public final HttpStatus httpStatus;

    public BaseAppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
