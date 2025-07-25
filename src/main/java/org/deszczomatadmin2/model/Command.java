package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Command{
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "device_id" , nullable = false)
    private Device device;
    private String commandPayload;
    private boolean executed = false;
    private LocalDateTime createdAt;

}