package org.repairflow.repairflowa.Security;


import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.repairflow.repairflowa.Services.JwtServices.JwtServices;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author guangyang
 * @date 11/12/25 AM1:36
 * @description TODO: Description
 */

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
                var userDetails = userDetailsService.loadUserByUsername(email);

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (JWTVerificationException ignored) {

            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String p = request.getServletPath();
        return p.equals("/register") || p.equals("/login") || p.equals("/logout");
    }
}
