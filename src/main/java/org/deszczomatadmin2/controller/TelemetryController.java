package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.DeviceUploadRequest;
import org.deszczomatadmin2.model.*;
import org.deszczomatadmin2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {
    @Autowired
    private UserRepository userRepo;
    @Autowired private TelemetryRepository telemetryRepo;
    @Autowired private CommandRepository commandRepo;
    @Autowired private DeviceSettingsRepository deviceSettingsRepo;
    @Autowired private DeviceRepository deviceRepo;

    @PostMapping("/device-sync")
    public ResponseEntity<Map<String, Object>> deviceSync(@RequestBody DeviceUploadRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.getUsername());

        if (userOpt.isEmpty() || !new BCryptPasswordEncoder().matches(req.getPassword(), userOpt.get().getHashed_password()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!userOpt.get().getRole().equals("DEVICE"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // Sprawdź czy urządzenie należy do użytkownika
        Optional<Device> deviceOpt = deviceRepo.findByDeviceId(String.valueOf(req.getDeviceId()));
        if (deviceOpt.isEmpty() || !deviceOpt.get().getOwnerUsername().equals(req.getUsername()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Urządzenie nie należy do użytkownika"));

        // Zapisz dane telemetryczne
        TelemetryData data = new TelemetryData();
        data.setDeviceId(req.getDeviceId());
        data.setPayload(req.getPayload());
        data.setTimestamp(LocalDateTime.now());
        telemetryRepo.save(data);

        // Pobierz oczekujące komendy
        List<Command> commands = commandRepo.findByDeviceIdAndExecutedFalse(String.valueOf(req.getDeviceId()));
        for (Command c : commands) c.setExecuted(true);
        commandRepo.saveAll(commands);

        // Sprawdź DeviceSettings
        Optional<DeviceSettings> settingsOpt = deviceSettingsRepo.findByDeviceId(String.valueOf(req.getDeviceId()));
        List<Map<String, String>> cmdList = new ArrayList<>();

        settingsOpt.ifPresent(settings -> {
            if (settings.isSpeedChangedPending()) {
                cmdList.add(Map.of("commandPayload", "{\"type\":\"SET_SPEED\",\"value\":" + settings.getSpeedLimit() + "}"));
                settings.setSpeedChangedPending(false);
                deviceSettingsRepo.save(settings);
            }
        });

        // Dodaj pozostałe komendy
        cmdList.addAll(commands.stream()
                .map(c -> Map.of("commandPayload", c.getCommandPayload()))
                .toList());

        return ResponseEntity.ok(Map.of("status", "OK", "commands", cmdList));
    }

//    @GetMapping("/device/{deviceId}")
//    public ResponseEntity<List<TelemetryData>> getDeviceData(@PathVariable String deviceId, Authentication auth) {
//        Optional<User> admin = userRepo.findByUsername(auth.getName());
//        if (admin.isEmpty() || !admin.get().getRole().equals("ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        return ResponseEntity.ok(telemetryRepo.findByDeviceId(deviceId));
//    }
}