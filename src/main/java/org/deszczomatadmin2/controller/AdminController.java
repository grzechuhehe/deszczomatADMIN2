package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.DeviceDTO;
import org.deszczomatadmin2.dto.UserDTO;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.User;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController{

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(DeviceRepository deviceRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users/add-user")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(userDTO.getRole());
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(toUserDTO(savedUser));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setUsername(userDTO.getUsername());
                    if(userDTO.getPassword() != null && ! userDTO.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }
                    user.setRole(userDTO.getRole());
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(toUserDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        if(user.getDevices() != null) {
            userDTO.setDevices(user.getDevices().stream()
                    .map(device -> new DeviceDTO(device.getId().intValue(), device.getDeviceId(), device.getOwner().getUsername()))
                    .collect(Collectors.toList()));
        }
        return userDTO;
    }


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        long userCount = userRepository.count();
        long deviceCount = deviceRepository.count();

        Map<String, Long> stats = Map.of(
                "users", userCount,
                "devices", deviceCount
        );

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/users/get-users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsernamesAndIds() {
        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("username", user.getUsername());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }


    @GetMapping("/devices/get-devices")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        return ResponseEntity.ok(deviceRepository.findAll().stream()
                .map(device -> new DeviceDTO(device.getId().intValue(), device.getDeviceId(), device.getOwner().getUsername()))
                .collect(Collectors.toList()));
    }


    @GetMapping("/devices/get-device/{userId}")
    public ResponseEntity<List<DeviceDTO>> getAllUserDevices(@PathVariable Long userId) {
        List<DeviceDTO> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOwner().getId().equals(userId))
                .map(device -> new DeviceDTO(
                        device.getId().intValue(),
                        device.getDeviceId(),
                        device.getOwner().getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDevices);
    }


    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if(userRepository.existsById(userId)) {
            // Decide what to do with user's devices. For now, we'll just delete the user.
            // In a real app, you might want to reassign them or delete them.
            userRepository.deleteById(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(toUserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/devices/add-device")
    public ResponseEntity<List<DeviceDTO>> createDevice(@RequestBody DeviceDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(user -> {
                    Device newDevice = new Device();
                    newDevice.setOwner(user);
                    newDevice.setDeviceId(request.getDeviceId());
                    deviceRepository.save(newDevice);

                    List<DeviceDTO> userDevices = deviceRepository.findAll().stream()
                            .filter(device -> device.getOwner().getId().equals(user.getId()))
                            .map(device -> new DeviceDTO(
                                    device.getId().intValue(),
                                    device.getDeviceId(),
                                    device.getOwner().getUsername()
                            ))
                            .collect(Collectors.toList());

                    return new ResponseEntity<>(userDevices, HttpStatus.CREATED);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }



}





