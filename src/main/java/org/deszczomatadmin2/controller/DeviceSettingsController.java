package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.model.DeviceSettings;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.DeviceSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices/{deviceId}/settings")
public class DeviceSettingsController {

    private final DeviceSettingsRepository deviceSettingsRepository;
    private final DeviceRepository deviceRepository;

    public DeviceSettingsController(DeviceSettingsRepository deviceSettingsRepository, DeviceRepository deviceRepository) {
        this.deviceSettingsRepository = deviceSettingsRepository;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping
    public ResponseEntity<DeviceSettings> getSettings(@PathVariable String deviceId, @AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByDeviceIdAndOwnerUsername(deviceId, userDetails.getUsername())
                .flatMap(device -> deviceSettingsRepository.findByDeviceId(device.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
