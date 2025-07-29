package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.model.Command;
import org.deszczomatadmin2.repository.CommandRepository;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CommandController {

    private final CommandRepository commandRepository;
    private final DeviceRepository deviceRepository;

    public CommandController(CommandRepository commandRepository, DeviceRepository deviceRepository) {
        this.commandRepository = commandRepository;
        this.deviceRepository = deviceRepository;
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
    public ResponseEntity<Command> createCommand(@RequestBody Command command) {
        return ResponseEntity.ok(commandRepository.save(command));
    }
}
