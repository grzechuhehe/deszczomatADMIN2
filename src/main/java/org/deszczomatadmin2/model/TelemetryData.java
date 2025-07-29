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
    private Long id;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
    private String payload;
    //TODO PAYLOAD - STATUS, DESIRED SPEED, CURRENT SPEED, DISTANCE, TIMETOEND, TIMEOFEND, AKU[V], WIATR, KIERUNEK WIATRU, CISNIENIE, TIME[ZWYKLY]
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime timestamp;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Device getDevice(){
        return device;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public String getPayload(){
        return payload;
    }

    public void setPayload(String payload){
        this.payload = payload;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

}
