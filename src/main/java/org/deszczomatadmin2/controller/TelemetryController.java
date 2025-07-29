package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.DeviceUploadRequest;
import org.deszczomatadmin2.model.*;
import org.deszczomatadmin2.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {
    @Autowired private UserRepository userRepo;
    @Autowired private TelemetryRepository telemetryRepo;
    @Autowired private CommandRepository commandRepo;
    @Autowired private DeviceSettingsRepository deviceSettingsRepo;
    @Autowired private DeviceRepository deviceRepo;

    @PostMapping("/device-sync")
    public ResponseEntity<Map<String, Object>> deviceSync(@RequestBody DeviceUploadRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.getUsername());

        if (userOpt.isEmpty() || !req.getPassword().equals(userOpt.get().getHashedPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userOpt.get();

        //TODO WARUNEK DOSTĘPU ADMIN
        if (!user.getRole().equals("DEVICE") && !user.getRole().equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // Sprawdź czy urządzenie należy do użytkownika
        Optional<Device> deviceOpt = deviceRepo.findByDeviceId(req.getDeviceId());

        if (deviceOpt.isEmpty() || !deviceOpt.get().getOwner().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Urządzenie nie należy do użytkownika"));

        Device device = deviceOpt.get();

        // Zapisz dane telemetryczne
        TelemetryData data = new TelemetryData();
        data.setId(req.getDeviceId());
        data.setPayload(req.getPayload());
        data.setTimestamp(LocalDateTime.now());
        telemetryRepo.save(data);

        // Pobierz oczekujące komendy
        //TODO - 1.WYSZUKAJ OSTATNIĄ NAJŚWIEŻSZĄ KOMENDE 2.SPRAWDZ CZY ZOSTAŁA WYSŁANA 3. JEŚLI TAK TO SPRAWDŹ CZY WYKONANA,
        /** znajdź najświeższą komendę
         if(lastcommandispresent){
         if(command.checkwasSent){
         if(command.getExectued) - JESZCZE BEZ TEGO{
         tzn że ok i tyle
         }
         else{
         if(
         }
         }
         else{
         send()
         }

         }*/
        List<Command> commands = commandRepo.findByDeviceIdAndExecutedFalse(device.getDeviceId());
        for (Command c : commands) c.setExecuted(true);
        commandRepo.saveAll(commands);

        // Sprawdź DeviceSettings
        Optional<DeviceSettings> settingsOpt = deviceSettingsRepo.findByDeviceId(device.getDeviceId());
        List<Map<String, String>> cmdList = new ArrayList<>();

        settingsOpt.ifPresent(settings -> {
            if (settings.isSpeedChangedPending()) {
                cmdList.add(Map.of("commandPayload", "{\"type\":\"SET_SPEED\",\"value\":" + settings.getSpeedLimit() + "}"));
                settings.setSpeedChangedPending(false);
                deviceSettingsRepo.save(settings);
            }
        });

        cmdList.addAll(commands.stream()
                .map(c -> Map.of("commandPayload", c.getCommandPayload()))
                .toList());

        return ResponseEntity.ok(Map.of("status", "OK", "commands", cmdList));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<TelemetryData>> getDeviceData(@PathVariable String deviceId, @RequestParam String username, @RequestParam String password) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty() || !password.equals(userOpt.get().getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userOpt.get();
        if (!user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(telemetryRepo.findByDevice(deviceRepo.getReferenceById(Long.valueOf(deviceId))));
    }
}