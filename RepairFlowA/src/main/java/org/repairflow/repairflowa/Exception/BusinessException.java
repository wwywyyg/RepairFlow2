package org.repairflow.repairflowa.Exception;

import lombok.Getter;

/**
 * @author guangyang
 * @date 12/9/25 21:50
 * @description TODO: Description
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Object... args ) {
        super(String.format(message, args));
        this.errorCode = errorCode;
    }
}
