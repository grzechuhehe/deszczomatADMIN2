package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSettings{
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "device_id", unique = true, nullable = false)
    private Device device;
    private Double speedLimit;
    private boolean speedChangedPending;
}
