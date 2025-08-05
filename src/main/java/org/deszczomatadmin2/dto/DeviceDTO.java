package org.deszczomatadmin2.dto;

public class DeviceDTO{
    private int id;
    private String deviceId;
    private String ownerUsername;

    public DeviceDTO(int id, String deviceId, String ownerUsername) {
        this.id = id;
        this.deviceId = deviceId;
        this.ownerUsername = ownerUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }


}
