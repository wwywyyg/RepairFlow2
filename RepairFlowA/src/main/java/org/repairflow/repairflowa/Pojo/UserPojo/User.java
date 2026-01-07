package org.repairflow.repairflowa.Pojo.UserPojo;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name" , nullable = false , length = 24)
    private String firstName;

    @Column(name = "last_name" , nullable = false , length = 24)
    private String lastName;

    @Column(name = "email" , nullable = false , unique = true,length = 24)
    private String email;

    @Column(name = "password_hash" , nullable = false , length = 120)
    private String passwordHash;

    @Column(name = "phone" , nullable = false , length = 24)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role" , nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToMany(mappedBy = "customer")
    private List<Ticket> customerTickets;

    @OneToMany(mappedBy = "employee")
    private List<Ticket> employeeTickets;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private OffsetDateTime updatedAt;




    //  User Details Class
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {return this.passwordHash;}

    @Override
    public String getUsername() {return this.email;}

    @Override
    public boolean isEnabled() {return this.isActive;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
