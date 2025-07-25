package org.deszczomatadmin2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_data")
public class TelemetryData{
    @Id
    @GeneratedValue
    private Long id;
    private String payload;
    private LocalDateTime timestamp;

}
