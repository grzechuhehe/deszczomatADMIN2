package org.deszczomatadmin2.model;

import jakarta.persistence.*;

@Entity
public class Device{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "deviceName")
    //TODO : String deviceName
    private String deviceName;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDeviceName() { return deviceName; }

    public void setDeviceName(String deviceId) { this.deviceName = deviceId; }

    public User getOwner() { return owner; }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
