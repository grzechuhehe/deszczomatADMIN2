package org.deszczomatadmin2.dto;

public class DeviceDTO{
    private int id;
    private String deviceName;
    private String ownerUsername;

    public DeviceDTO(int id, String deviceName, String ownerUsername) {
        this.id = id;
        this.deviceName = deviceName;
        this.ownerUsername = ownerUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }



}
