package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Device{
    @Id
    @GeneratedValue
    private Long id;
    private String deviceId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
