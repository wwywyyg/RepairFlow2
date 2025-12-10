package org.repairflow.repairflowa.Exception;


import org.springframework.http.HttpStatus;

public final class AppExceptions {
    private AppExceptions() {}

    /**
     * 400 - Bad Request
     */
    public static class BadRequestException extends BaseAppException {
        public BadRequestException(String message) {
            super(message, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 403 - Refusal to authorize
     */

    public static class ForbiddenException extends BaseAppException {
        public ForbiddenException(String message) {
            super(message, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * 404 - resource not found
     */
    public static class ResourceNotFoundException extends BaseAppException {
        public ResourceNotFoundException(String message) {
            super(message, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 409 - data conflict
     */
    public static class DataConflictException extends BaseAppException {
        public DataConflictException(String message) {
            super(message, HttpStatus.CONFLICT);
        }
    }

    /**
     * 422 - valid / parameter error
     */
    public static class ValidationException extends BaseAppException {
        public ValidationException(String message) {
            super(message, HttpStatus.BAD_REQUEST);
        }
    }


}
