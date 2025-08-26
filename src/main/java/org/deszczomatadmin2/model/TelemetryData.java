package org.deszczomatadmin2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.deszczomatadmin2.dto.TelemetryDataDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_data")
public class TelemetryData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    private Integer status;
    private Integer desiredSpeed;
    private Integer currentSpeed;
    private Integer distance;
    private Integer timeToEnd;
    private Integer timeOfEnd;
    private Integer akuVoltage;
    private Integer windSpeed;
    private Integer windDirection;
    private Integer pressure;

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }

    private Integer alert;

    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDesiredSpeed() {
        return desiredSpeed;
    }

    public void setDesiredSpeed(Integer desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    public Integer getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Integer currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getTimeToEnd() {
        return timeToEnd;
    }

    public void setTimeToEnd(Integer timeToEnd) {
        this.timeToEnd = timeToEnd;
    }

    public Integer getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Integer windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Integer getTimeOfEnd() {
        return timeOfEnd;
    }

    public void setTimeOfEnd(Integer timeOfEnd) {
        this.timeOfEnd = timeOfEnd;
    }

    public Integer getAkuVoltage() {
        return akuVoltage;
    }

    public void setAkuVoltage(Integer akuVoltage) {
        this.akuVoltage = akuVoltage;
    }

    public Integer getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Integer windDirection) {
        this.windDirection = windDirection;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public String getTimestamp() {
        return timestamp.toString();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getJsonString() {
        TelemetryDataDTO dto = new TelemetryDataDTO();
        dto.deviceName=device.getDeviceName();
        dto.sta=status;
        dto.spd_c=currentSpeed;
        dto.spd_d=desiredSpeed;
        dto.dis=distance;
        dto.toe=timeOfEnd;
        dto.tte=timeToEnd;
        dto.bat=akuVoltage;
        dto.wds=windSpeed;
        dto.wdd=windDirection;
        dto.ps=pressure;
        dto.alt=alert;
        dto.tmstmp=timestamp.toString();

        try {
            String json = new ObjectMapper().writeValueAsString(dto);
            return json;
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
