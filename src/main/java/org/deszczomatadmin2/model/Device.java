package org.deszczomatadmin2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Device{
    @Id
    @GeneratedValue
    private Long id;
    private String deviceId;
    private String ownerUsername;
}
