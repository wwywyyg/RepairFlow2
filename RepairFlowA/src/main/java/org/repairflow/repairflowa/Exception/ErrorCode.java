package org.repairflow.repairflowa.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author guangyang
 * @date 12/9/25 21:45
 * @description TODO: Description
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // General Error
    INTERNAL_SERVER_ERROR(500, "System internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(400, "Bad request parameters", HttpStatus.BAD_REQUEST),

    // Business Module   -----   USER
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_FOUND(404, "Employee not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXIST(409, "User already exists", HttpStatus.CONFLICT),
    USER_PASSWORD_ERROR(400, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    USER_RIGHT_CONFLICT(409, "User can not do this operation", HttpStatus.CONFLICT),


    //Business Module ---- TICKET
    TICKET_NOT_FOUND(404, "Ticket not found", HttpStatus.NOT_FOUND),
    TICKET_ALREADY_CLAIMED(409002, "Ticket has been claimed by others", HttpStatus.CONFLICT),
    TICKET_DATA_CONFLICT(409003,"Ticket not belong to you", HttpStatus.CONFLICT),
    TICKET_STATUS_INVALID(400003, "Ticket status is not valid for this operation", HttpStatus.BAD_REQUEST),
    NO_PERMISSION_TO_CLAIM(403001, "You are not allowed to claim this ticket", HttpStatus.FORBIDDEN),
    TICKET_NOT_OWN(403002, "You are not own this ticket", HttpStatus.FORBIDDEN),
    TICKET_SET_STATUS_CONFLICT(400004,"Ticket set status is not valid for this operation", HttpStatus.CONFLICT),;

    private final int code;          // 业务自定义状态码 (传给前端用于判断逻辑)
    private final String message;    // 错误信息
    private final HttpStatus status; // HTTP 状态码 (给浏览器和网关看)
}
