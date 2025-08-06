package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.DeviceDTO;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceController(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody DeviceDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(user -> {
                    Device newDevice = new Device();
                    newDevice.setOwner(user);
                    newDevice.setDeviceId(request.getDeviceId());
                    Device savedDevice = deviceRepository.save(newDevice);
                    return new ResponseEntity<>(savedDevice, HttpStatus.CREATED);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Device> getDevice(@PathVariable String deviceId, @AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByDeviceIdAndOwnerUsername(deviceId, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<Device> updateDevice(@PathVariable String deviceId, @RequestBody DeviceDTO updatedDevice, @AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByDeviceIdAndOwnerUsername(deviceId, userDetails.getUsername())
                .map(device -> {
                    device.setDeviceId(updatedDevice.getDeviceId());
                    Device savedDevice = deviceRepository.save(device);
                    return ResponseEntity.ok(savedDevice);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<List<DeviceDTO>> deleteDevice(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Device> optionalDevice = deviceRepository.findByIdAndOwnerUsername(id, userDetails.getUsername());

        if (optionalDevice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        deviceRepository.delete(optionalDevice.get());

        List<DeviceDTO> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOwner().getUsername().equals(userDetails.getUsername()))
                .map(device -> new DeviceDTO(
                        device.getId().intValue(),
                        device.getDeviceId(),
                        device.getOwner().getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDevices);
    }


}