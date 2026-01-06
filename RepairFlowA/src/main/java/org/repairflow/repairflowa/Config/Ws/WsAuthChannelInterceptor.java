package org.repairflow.repairflowa.Config.Ws;


import org.repairflow.repairflowa.Services.JwtServices.JwtServices;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author guangyang
 * @date 1/2/26 15:33
 * @description TODO: Description
 */

@Component
public class WsAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtServices jwtServices;

    public WsAuthChannelInterceptor(JwtServices jwtServices) {
        this.jwtServices = jwtServices;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String auth = null;
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) auth = authHeaders.get(0);

            if (auth == null || !auth.startsWith("Bearer ")) {
                throw new org.springframework.messaging.MessagingException("Missing/invalid Authorization header");
            }

            String token = auth.substring(7);

            try {
                String email = jwtServices.extractEmail(token);
                Long userId = jwtServices.extractUserId(token);
                String role = jwtServices.extractRole(token);

                WsPrincipal principal = new WsPrincipal(userId, email, role);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.authorities()
                );

                accessor.setUser(authentication);

            } catch (Exception e) {
                throw new org.springframework.messaging.MessagingException("Invalid JWT Token", e);
            }
        }

        return message;
    }
}
