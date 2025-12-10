package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto;

import lombok.Builder;
import lombok.Data;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author guangyang
 * @date 11/25/25 AM6:42
 * @description TODO: Description
 */
@Data
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private Long deviceCategoryId;
    private Long issueTypeId;
    private TicketStatus status;
    private BigDecimal quoteAmount;
    private boolean paid;

    private SimpleUserResp customer;
    private SimpleUserResp employee;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data @Builder
    public static class SimpleUserResp {
        private Long id;
        private String name;
        private String email;
    }
}
