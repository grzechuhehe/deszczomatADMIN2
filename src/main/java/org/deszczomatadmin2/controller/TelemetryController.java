package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.TelemetryData;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.TelemetryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;

    public TelemetryController(TelemetryRepository telemetryRepository, DeviceRepository deviceRepository) {
        this.telemetryRepository = telemetryRepository;
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadTelemetry(@RequestBody TelemetryData telemetryData, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Device> deviceOptional = deviceRepository.findByOwner_Username(userDetails.getUsername());
        if (deviceOptional.isPresent()) {
            telemetryData.setDevice(deviceOptional.get());
            telemetryRepository.save(telemetryData);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
