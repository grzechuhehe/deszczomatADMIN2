package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_data")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TelemetryData{
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
    private String payload;
    private LocalDateTime timestamp;

}
