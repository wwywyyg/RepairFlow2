package org.repairflow.repairflowa.Security;


import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.repairflow.repairflowa.Services.JwtServices.JwtServices;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author guangyang
 * @date 11/12/25 AM1:36
 * @description TODO: Description
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtServices jwtServices;

    private final UserDetailsServiceImpl userDetailsService;



    public JwtAuthFilter(JwtServices jwtServices, UserDetailsServiceImpl userDetailsService) {
        this.jwtServices = jwtServices;
        this.userDetailsService = userDetailsService ;


    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try{
                var decoded = jwtServices.verifyToken(token);
                String email = decoded.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UserPrincipal userPrincipal = (UserPrincipal) userDetails;

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (JWTVerificationException e) {
                log.warn("JWT verification failed: {}",e.getMessage());

            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String p = request.getServletPath();

        if (p.startsWith("/ws")) return true;
        return p.equals("/auth/register") || p.equals("/auth/login") || p.equals("/auth/logout");
    }
}
