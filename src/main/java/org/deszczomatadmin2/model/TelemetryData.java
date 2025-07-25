package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import jdk.jfr.StackTrace;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_data")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class TelemetryData{
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    private Long deviceId;
    private String payload;
    private LocalDateTime timestamp;

}
