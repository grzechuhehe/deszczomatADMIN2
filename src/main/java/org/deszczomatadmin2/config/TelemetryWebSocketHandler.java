package org.deszczomatadmin2.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TelemetryWebSocketHandler implements WebSocketHandler {

    // Mapa userId do listy sesji
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long)session.getAttributes().get("userId");
        if (userId != null) {
            sessionsByUserId.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(session);
            System.out.println("Connected session for userId: " + userId);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No userId"));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Możesz obsłużyć wiadomości od klienta, jeśli chcesz
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error: " + exception.getMessage());
        removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        removeSession(session);
        System.out.println("Connection closed: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void removeSession(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            List<WebSocketSession> sessions = sessionsByUserId.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionsByUserId.remove(userId);
                }
            }
        }
    }

    // Metoda do wysyłania danych telemetrycznych JSON do danego userId
    public void sendTelemetryToUser(Long userId, String telemetryJson) {
        List<WebSocketSession> sessions = sessionsByUserId.get(userId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(telemetryJson));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
