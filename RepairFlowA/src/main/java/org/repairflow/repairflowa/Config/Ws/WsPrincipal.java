package org.repairflow.repairflowa.Config.Ws;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;

/**
 * @author guangyang
 * @date 1/2/26 15:25
 * @description TODO: Description
 */
public class WsPrincipal implements Principal {


    private final Long userId;
    private final String email;
    private final String role;

    public WsPrincipal(Long userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getName() {
        return email;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public List<GrantedAuthority> authorities() {
    String normalize = role.startsWith("ROLE_")? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(normalize));
    }

}
