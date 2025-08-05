package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.TelemetryRequest;
import org.deszczomatadmin2.model.Command;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.TelemetryData;
import org.deszczomatadmin2.repository.CommandRepository;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.TelemetryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;
    private final CommandRepository commandRepository;
    private static final Logger log = LoggerFactory.getLogger(TelemetryController.class);

    public TelemetryController(TelemetryRepository telemetryRepository, DeviceRepository deviceRepository, CommandRepository commandRepository) {
        this.telemetryRepository = telemetryRepository;
        this.deviceRepository = deviceRepository;
        this.commandRepository = commandRepository;
    }


    @PostMapping("/device-sync")
    public ResponseEntity<Void> uploadTelemetry(
            @RequestBody TelemetryRequest dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Device> deviceOptional = deviceRepository.findByDeviceIdAndOwnerUsername(dto.deviceId, userDetails.getUsername());
//        log.debug("hubert" +dto.deviceId);
        if (deviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Device device = deviceOptional.get();
        String komenda = checkForCommand(device,null);
        HttpHeaders headers = new HttpHeaders();
        if(komenda != null) {
            headers = commandBuilder(komenda);
        }
        TelemetryData telemetry = new TelemetryData();
        telemetry.setDevice(device);
        telemetry.setStatus(dto.sta);
        telemetry.setCurrentSpeed(dto.spd_c);
        telemetry.setDesiredSpeed(dto.spd_d);
        telemetry.setDistance(dto.dis);
        telemetry.setTimeToEnd(dto.tte);
        telemetry.setAkuVoltage(dto.bat);
        telemetry.setWindSpeed(dto.wds);
        telemetry.setWindDirection(dto.wdd);
        telemetry.setPressure(dto.ps);
        telemetry.setTimestamp(LocalDateTime.now());

        telemetryRepository.save(telemetry);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    public HttpHeaders commandBuilder(String value) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("CMD",value);
        return responseHeaders;
    }


    public String checkForCommand(Device device, TelemetryData telemetry) {
        Optional<Command> commandOptional = commandRepository.findFirstByDeviceAndExecutedIsFalseOrderByCreatedAtDesc(device);
        if (commandOptional.isPresent()) {
            Command cmd = commandOptional.get();

            // oznacz jako wykonane, jeśli trzeba
            // cmd.setExecuted(true);
            commandRepository.save(cmd);

            Integer command = cmd.getCommandNumber();
            String value = cmd.getCommandPayload();
            return new String(String.valueOf(command)+" VAL:"+value+"/VAL");
        }
        return null;
    }

}
