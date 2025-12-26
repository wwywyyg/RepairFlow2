package org.repairflow.repairflowa.Pojo.TicketPojo;

/**
 * @author guangyang
 * @date 11/25/25 AM6:03
 * @description TODO: Description
 */
public enum TicketStatus {
    PENDING,  // CREATED
    ASSIGNED,
    QUOTED,
    AWAITING_DEVICE, // AWAITING
    DEVICE_RECEIVED, // RECEIVED
    IN_PROGRESS, // RPROGRESSING
    READY_FOR_CONFIRMATION, //COMPELETED
    PAID,
    SHIPPED,
    DELIVERED
}
