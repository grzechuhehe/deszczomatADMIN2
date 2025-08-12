package org.deszczomatadmin2.config;

import org.deszczomatadmin2.controller.TelemetryController;
import org.deszczomatadmin2.model.User;
import org.deszczomatadmin2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private Long getUserIdFromAuth(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            log.debug("Before handshake5");
            User user = userOpt.get();
            passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password,user.getPassword())) { // porównanie wprost
                log.debug("Before handshake6");
                return user.getId(); // zwracamy user_id z bazy
            }
        }
        return null; // brak zgodności
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.debug("Before handshake");
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey("Authorization")) {
            log.debug("Before handshake1");
            String authHeader = headers.getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                log.debug("Before handshake2");
                String base64Credentials = authHeader.substring("Basic ".length());
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                String[] values = credentials.split(":", 2);

                String username = values[0];
                String password = values[1];

                Long userId = getUserIdFromAuth(username, password);
                if (userId != null) {
                    log.debug("Before handshake3");
                    attributes.put("userId", userId);
                    return true; // autoryzacja OK
                }
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // nic do zrobienia po handshake
    }
}
