package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.DeviceDTO;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    public DeviceController(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

//    @PostMapping("/add-device")
//    public ResponseEntity<Object> createDevice(@RequestBody DeviceDTO request,
//                                               @AuthenticationPrincipal UserDetails userDetails) {
//        return userRepository.findByUsername(userDetails.getUsername())
//                .map(user -> {
//                    Device newDevice = new Device();
//                    newDevice.setOwner(user);
//                    newDevice.setDeviceName(request.getDeviceName());
//                    deviceRepository.save(newDevice);
//                    return ResponseEntity.ok().build(); // OK bez body
//                })
//                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // Nie OK
//    }

    @PostMapping("/add-device")
    public ResponseEntity<Object> createDevice(@RequestBody DeviceDTO request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(user -> {
                    long currentCount = deviceRepository.countByOwnerId(user.getId());
                    Integer max = user.getMaxDevices();
                    int maxAllowed = (max != null) ? max : Integer.MAX_VALUE;

                    if (currentCount >= maxAllowed) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }

                    Device newDevice = new Device();
                    newDevice.setOwner(user);
                    newDevice.setDeviceName(request.getDeviceName());

                    Device saved = deviceRepository.save(newDevice); // zapewnij ID

                    String jsonString = String.format("{\"deviceId\": %d}", saved.getId());

                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body((Object) jsonString);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }





    @GetMapping("/{deviceId}")
    public ResponseEntity<Device> getDevice(@PathVariable String deviceName, @AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByDeviceNameAndOwnerUsername(deviceName, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/edit/{deviceId}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long deviceId, @RequestBody DeviceDTO updatedDevice, @AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByIdAndOwnerUsername(deviceId, userDetails.getUsername())
                .map(device -> {
                    device.setDeviceName(updatedDevice.getDeviceName());
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
                        device.getDeviceName(),
                        device.getOwner().getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDevices);
    }

    @GetMapping("/get-device/{userId}")
    public ResponseEntity<List<DeviceDTO>> getAllUserDevices(@PathVariable Long userId) {
        List<DeviceDTO> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOwner().getId().equals(userId))
                .map(device -> new DeviceDTO(
                        device.getId().intValue(),
                        device.getDeviceName(),
                        device.getOwner().getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDevices);
    }


}