package org.repairflow.repairflowa.Pojo.TicketPojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.repairflow.repairflowa.Pojo.UserPojo.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author guangyang
 * @date 11/23/25 AM4:42
 * @description TODO: Description
 */
@Data
@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title" , nullable = false , length = 64)
    private String title;


    @Column(name = "description" , nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name="device_category_id")
    private Long deviceCategoryId;

    @Column(name="issue_type_id")
    private Long issueTypeId;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "customer_id",nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 40)
    private TicketStatus status;

    @Column( nullable = false,precision = 10,scale = 2)
    private BigDecimal quoteAmount;

    @Column(nullable = false)
    private boolean paid;




    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(this.status == null) this.status = TicketStatus.PENDING;
        if(this.quoteAmount == null) this.quoteAmount = BigDecimal.ZERO;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
