package org.repairflow.repairflowa.Security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author guangyang
 * @date 11/13/25 AM3:52
 * @description TODO: Description
 */
@Component
public class UserGuard {

    public boolean isSelf(Long userId) {
        var ctx = SecurityContextHolder.getContext();
        var auth = ctx.getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        if (auth instanceof AnonymousAuthenticationToken) return false;

        Object principal = auth.getPrincipal();

        // 管理员直接放行（也可在 @PreAuthorize 写 hasRole('ADMIN')，两种都行，二选一即可）
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        if (principal instanceof UserPrincipal up) {
            return up.getUserId() != null && up.getUserId().equals(userId);
        }

        // 兜底：如果还不是我们的 principal，就拒绝
        return false;
    }
}
