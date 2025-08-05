package org.deszczomatadmin2.model;

import jakarta.persistence.*;

@Entity
public class Device{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "deviceId")
    private String deviceId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public User getOwner() { return owner; }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
