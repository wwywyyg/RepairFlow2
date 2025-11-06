package org.repairflow.repairflowa.Exception;


public final class AppExceptions {
    private AppExceptions() {}


    /**
     * 404 - resource not found
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 409 - data conflict
     */
    public static class DataConflictException extends RuntimeException {
        public DataConflictException(String message) {
            super(message);
        }
    }

    /**
     * 422 - valid / parameter error
     */
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
