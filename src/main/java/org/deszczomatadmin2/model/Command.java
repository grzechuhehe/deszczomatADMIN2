package org.deszczomatadmin2.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Command {
    @Getter
    @Id
    @GeneratedValue
    private Long id;
    @Getter
    @ManyToOne
    @JoinColumn(name = "device_id" , nullable = false)
    private Device device;
    private String commandPayload;
    private boolean executed = false;
    //TODO - ATRYBUT CZY KOMENDA JEST WYKONANA(WAS SENT)
    //TODO - ATRYBUT LICZNIK PONOWIEŃ WYSŁANIA(INKRETMENTALNY) -
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getCommandPayload() {
        return commandPayload;
    }

    public void setCommandPayload(String commandPayload) { this.commandPayload = commandPayload; }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}