package org.deszczomatadmin2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TelemetryWebSocketHandler telemetryWebSocketHandler;

    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(telemetryWebSocketHandler, "/ws/telemetry")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*"); // w prod lepiej dać konkretne adresy
    }
}
