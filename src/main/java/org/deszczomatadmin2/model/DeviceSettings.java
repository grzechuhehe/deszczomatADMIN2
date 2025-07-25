package org.deszczomatadmin2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class DeviceSettings{
    @Id
    @GeneratedValue
    private Long id;
    private String deviceId;
    private Double speedLimit;
    private boolean speedChangedPending;
}
