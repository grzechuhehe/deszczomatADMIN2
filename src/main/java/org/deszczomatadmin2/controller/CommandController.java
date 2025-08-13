package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.model.Command;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.User;
import org.deszczomatadmin2.repository.CommandRepository;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController("/command")
public class CommandController {

    private final CommandRepository commandRepository;
    private final DeviceRepository deviceRepository;
//    private static final Logger log = LoggerFactory.getLogger(CommandController.class);
    private final UserRepository userRepository;

    public CommandController(CommandRepository commandRepository, DeviceRepository deviceRepository, UserRepository userRepository) {
        this.commandRepository = commandRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("api/user/devices")
    public ResponseEntity<List<String>> getDeviceIdsByUsername(@RequestHeader("username") String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        List<String> deviceIds = deviceRepository.findByOwner(user).stream()
                .map(Device::getDeviceName)
                .toList();

        return ResponseEntity.ok(deviceIds);
    }


    @GetMapping("/api/commands/next")
    public ResponseEntity<Command> getNextCommand(@AuthenticationPrincipal UserDetails userDetails) {
        return deviceRepository.findByOwner_Username(userDetails.getUsername())
                .flatMap(device -> commandRepository.findFirstByDeviceAndExecutedIsFalseOrderByCreatedAtDesc(device))
                .map(command -> {
                    command.setRetryCount(command.getRetryCount() + 1);
                    commandRepository.save(command);
                    return ResponseEntity.ok(command);
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/api/commands/{id}/executed")
    public ResponseEntity<Void> markCommandAsExecuted(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Command> commandOptional = commandRepository.findById(id);
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();
            if (command.getDevice().getOwner().getUsername().equals(userDetails.getUsername())) {
                command.setExecuted(true);
                commandRepository.save(command);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/admin/commands")
    @ResponseStatus(HttpStatus.CREATED)
    public Command createCommand(@RequestBody Command command) {
        return commandRepository.save(command);
    }

    // DTO
    public record DeviceBrief(Long id, String deviceId, String deviceName) {}
    public record UserResponse(Long id, String username, String role, List<DeviceBrief> devices) {}

    // Controller
    @GetMapping("api/login")
    public ResponseEntity<UserResponse> getAdminApp(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(u -> {
                    List<DeviceBrief> devices = (u.getDevices() == null) ? List.of()
                            : u.getDevices().stream()
                            .map(d -> new DeviceBrief(d.getId(), d.getOwner().toString(), d.getDeviceName()))
                            .toList();

                    return ResponseEntity.ok(new UserResponse(
                            u.getId(),
                            u.getUsername(),
                            u.getRole(),
                            devices
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}
