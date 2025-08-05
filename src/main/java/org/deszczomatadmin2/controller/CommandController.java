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

@RestController
public class CommandController {

    private final CommandRepository commandRepository;
    private final DeviceRepository deviceRepository;
    private static final Logger log = LoggerFactory.getLogger(CommandController.class);
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
                .map(Device::getDeviceId)
                .toList();

        return ResponseEntity.ok(deviceIds);
    }

    @GetMapping("api/admin/stats")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        long userCount = userRepository.count();
        long deviceCount = deviceRepository.count();

        Map<String, Long> stats = Map.of(
                "users", userCount,
                "devices", deviceCount
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/admin/get_users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        long userCount = userRepository.count();
        List<String> usernames = userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .toList();

        Map<String, Object> result = Map.of(
                "userCount", userCount,
                "usernames", usernames
        );

        return ResponseEntity.ok(result);
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

    @GetMapping("api/adminapp")
    public ResponseEntity<Command> getAdminApp(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().build();
    }
}
