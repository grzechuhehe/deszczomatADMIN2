package org.deszczomatadmin2.controller;

import org.deszczomatadmin2.dto.TelemetryChartDataDTO;
import org.deszczomatadmin2.dto.TelemetryDataDTO;
import org.deszczomatadmin2.model.Command;
import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.TelemetryData;
import org.deszczomatadmin2.repository.CommandRepository;
import org.deszczomatadmin2.repository.DeviceRepository;
import org.deszczomatadmin2.repository.TelemetryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("telemetry")
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
            @RequestBody TelemetryDataDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Device> deviceOptional = deviceRepository.findByDeviceNameAndOwnerUsername(dto.deviceName, userDetails.getUsername());
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
        telemetry.setTimeOfEnd(dto.toe);
        telemetry.setTimeToEnd(dto.tte);
        telemetry.setAkuVoltage(dto.bat);
        telemetry.setWindSpeed(dto.wds);
        telemetry.setWindDirection(dto.wdd);
        telemetry.setPressure(dto.ps);
        telemetry.setAlert(dto.alt);
        telemetry.setTimestamp(LocalDateTime.now());

        telemetryRepository.save(telemetry);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTelemetryForDevice(
            @PathVariable Long id,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String resolution,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Device> deviceOptional = deviceRepository.findByIdAndOwnerUsername(id, userDetails.getUsername());
        if (deviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (channel != null && from != null && to != null) {
            List<TelemetryData> telemetryDataList = telemetryRepository.findByDeviceIdAndTimestampBetween(id, from, to);
            List<TelemetryChartDataDTO> chartData = telemetryDataList.stream()
                    .map(data -> new TelemetryChartDataDTO(LocalDateTime.parse(data.getTimestamp()), getChannelValue(data, channel)))
                    .collect(Collectors.toList());

            if (resolution != null && !resolution.equalsIgnoreCase("raw")) {
                chartData = aggregateData(chartData, resolution);
            }

            return ResponseEntity.ok(chartData);
        } else {
            List<TelemetryData> telemetryDataList = telemetryRepository.findByDevice_Id(id);
            List<TelemetryDataDTO> telemetryDataDTOList = telemetryDataList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(telemetryDataDTOList);
        }
    }

    private List<TelemetryChartDataDTO> aggregateData(List<TelemetryChartDataDTO> data, String resolution) {
        if (data.isEmpty() || !(data.get(0).getValue() instanceof Integer)) {
            return data; // Agregacja tylko dla danych numerycznych (Integer)
        }

        long intervalMinutes;
        switch (resolution.toLowerCase()) {
            case "15m":
                intervalMinutes = 15;
                break;
            case "1h":
                intervalMinutes = 60;
                break;
            case "3h":
                intervalMinutes = 180;
                break;
            case "6h":
                intervalMinutes = 360;
                break;
            case "1d":
                intervalMinutes = 1440; // 24 * 60
                break;
            default:
                return data; // Nieznana rozdzielczość, zwróć surowe dane
        }

        Map<LocalDateTime, List<Integer>> groupedData = data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getTimestamp().truncatedTo(ChronoUnit.MINUTES).withMinute(0).withSecond(0).withNano(0)
                                .plusMinutes(intervalMinutes * (d.getTimestamp().getMinute() / intervalMinutes)),
                        LinkedHashMap::new,
                        Collectors.mapping(d -> (Integer) d.getValue(), Collectors.toList())
                ));

        return groupedData.entrySet().stream()
                .map(entry -> {
                    double average = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0);
                    return new TelemetryChartDataDTO(entry.getKey(), (int) Math.round(average));
                })
                .collect(Collectors.toList());
    }

    private Object getChannelValue(TelemetryData data, String channel) {
        switch (channel) {
            case "bat":
                return data.getAkuVoltage();
            case "wds":
                return data.getWindSpeed();
            case "dis":
                return data.getDistance();
            case "ps":
                return data.getPressure();
            case "spd_c":
                return data.getCurrentSpeed();
            case "spd_d":
                return data.getDesiredSpeed();
            case "sta":
                return data.getStatus();
            case "toe":
                return data.getTimeOfEnd();
            case "tte":
                return data.getTimeToEnd();
            case "wdd":
                return data.getWindDirection();
            case "alt":
                return data.getAlert();
            default:
                return null;
        }
    }


    private TelemetryDataDTO convertToDto(TelemetryData telemetryData) {
        TelemetryDataDTO dto = new TelemetryDataDTO();
        dto.id = telemetryData.getId();
        dto.deviceName = telemetryData.getDevice().getDeviceName();
        dto.sta = telemetryData.getStatus();
        dto.spd_c = telemetryData.getCurrentSpeed();
        dto.spd_d = telemetryData.getDesiredSpeed();
        dto.dis = telemetryData.getDistance();
        dto.toe = telemetryData.getTimeOfEnd();
        dto.tte = telemetryData.getTimeToEnd();
        dto.bat = telemetryData.getAkuVoltage();
        dto.wds = telemetryData.getWindSpeed();
        dto.wdd = telemetryData.getWindDirection();
        dto.ps = telemetryData.getPressure();
        dto.alt = telemetryData.getAlert();
        dto.tmstmp=telemetryData.getTimestamp();
        return dto;
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
