package org.deszczomatadmin2.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.deszczomatadmin2.dto.TelemetryDataDTO;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.TelemetryData;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.TelemetryRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TelemetryWebSocketHandler implements WebSocketHandler {

    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();
    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    public TelemetryWebSocketHandler(TelemetryRepository telemetryRepository, DeviceRepository deviceRepository, ObjectMapper objectMapper) {
        this.telemetryRepository = telemetryRepository;
        this.objectMapper = objectMapper;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No userId"));
            return;
        }

        sessionsByUserId.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("Connected session for userId: " + userId);

        // 1) pobierz wszystkie urządzenia użytkownika
        List<Device> devices = deviceRepository.findAllByOwnerId(userId);

        // 2) dla każdego urządzenia wyślij najświeższy rekord telemetrii
        for (Device device : devices) {
            Optional<TelemetryData> latest =
                    telemetryRepository.findTopByDeviceIdOrderByTimestampDesc(device.getId());
            if (latest.isPresent()) {
                TelemetryData t = latest.get();

                try {
                    // 1. pobierz oryginalny JSON jako String
                    String telemetryJson = t.getJsonString();

                    // 2. zdeserializuj do mapy
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> map = mapper.readValue(telemetryJson, new TypeReference<>() {});

                    // 3. dodaj deviceId
                    map.put("deviceId", device.getId());

                    // 4. zserializuj z powrotem do Stringa
                    String finalJson = mapper.writeValueAsString(map);

                    // 5. wyślij przez WebSocket
                    sendTelemetryToUser(userId, finalJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void sendLastTelemetryData(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return;
        }

        String path = uri.getPath();
        String[] segments = path.split("/");
        if (segments.length > 0) {
            try {
                Long deviceId = Long.parseLong(segments[segments.length - 1]);
                Optional<TelemetryData> lastData = telemetryRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId);

                if (lastData.isPresent()) {
                    TelemetryData data = lastData.get();
                    TelemetryDataDTO dto = new TelemetryDataDTO(
                            data.getTimestamp(),
                            data.getId(),
                            data.getDevice().getDeviceName(),
                            data.getStatus(),
                            data.getDesiredSpeed(),
                            data.getTimeOfEnd(),
                            data.getCurrentSpeed(),
                            data.getDistance(),
                            data.getTimeToEnd(),
                            data.getWindSpeed(),
                            data.getAkuVoltage(),
                            data.getWindDirection(),
                            data.getPressure(),
                            data.getAlert()
                    );
                    String json = objectMapper.writeValueAsString(dto);
                    session.sendMessage(new TextMessage(json));
                }
            } catch (NumberFormatException e) {
                System.err.println("Could not parse deviceId from URI: " + path);
            } catch (IOException e) {
                System.err.println("Error sending last telemetry data: " + e.getMessage());
            }
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
