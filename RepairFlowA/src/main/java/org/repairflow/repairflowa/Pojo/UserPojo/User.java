package org.repairflow.repairflowa.Pojo.UserPojo;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Table(name = "users")
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name" , nullable = false , length = 24)
    private String firstName;

    @Column(name = "last_name" , nullable = false , length = 24)
    private String lastName;

    @Column(name = "email" , nullable = false , unique = true,length = 24)
    private String email;

    @Column(name = "password_hash" , nullable = false , length = 24)
    private String passwordHash;

    @Column(name = "phone" , nullable = false , length = 24)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role" , nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(name = "is_active")
    private boolean isActive = true;

//    @OneToMany(mappedBy = "customer")
//    private List<Ticket> customerTickets;
//
//    @OneToMany(mappedBy = "customer")
//    private List<Ticket> employeeTickets;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @CreationTimestamp
    @Column(name = "update_at", nullable = false)
    private OffsetDateTime updatedAt;

}
