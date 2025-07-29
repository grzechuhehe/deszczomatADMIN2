package org.deszczomatadmin2.model;

import jakarta.persistence.*;

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

    private String status;
    private Float desiredSpeed;
    private Float currentSpeed;
    private Float distance;
    private Integer timeToEnd;
    private LocalDateTime timeOfEnd;
    private Float akuVoltage;
    private Float windSpeed;
    private String windDirection;
    private Float pressure;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getDesiredSpeed() {
        return desiredSpeed;
    }

    public void setDesiredSpeed(Float desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    public Float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getTimeToEnd() {
        return timeToEnd;
    }

    public void setTimeToEnd(Integer timeToEnd) {
        this.timeToEnd = timeToEnd;
    }

    public Float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public LocalDateTime getTimeOfEnd() {
        return timeOfEnd;
    }

    public void setTimeOfEnd(LocalDateTime timeOfEnd) {
        this.timeOfEnd = timeOfEnd;
    }

    public Float getAkuVoltage() {
        return akuVoltage;
    }

    public void setAkuVoltage(Float akuVoltage) {
        this.akuVoltage = akuVoltage;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
