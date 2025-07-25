package org.deszczomatadmin2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Command{
    @Id
    @GeneratedValue
    private Long id;
    private String deviceId;
    private String commandPayload;
    private boolean executed = false;
    private LocalDateTime createdAt;

}