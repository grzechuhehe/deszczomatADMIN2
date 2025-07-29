package org.deszczomatadmin2.model;

import jakarta.persistence.*;


@Entity
public class DeviceSettings {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "device_id", unique = true, nullable = false)
    private Device device;
    private Double speedLimit;
    private boolean speedChangedPending;

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public boolean isSpeedChangedPending() {
        return speedChangedPending;
    }

    public void setSpeedChangedPending(boolean speedChangedPending) {
        this.speedChangedPending = speedChangedPending;
    }


}
