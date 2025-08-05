package org.deszczomatadmin2.dto;

import java.time.LocalDateTime;

public class CommandDTO{

    private Long id;
    private Long device_Id;
    private String commandPayload;
    private Integer commandNumber;
    private boolean executed;
    private Integer retryCount;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDevice_Id() {
        return device_Id;
    }

    public void setDevice_Id(Long device_Id) {
        this.device_Id = device_Id;
    }

    public String getCommandPayload() {
        return commandPayload;
    }

    public void setCommandPayload(String commandPayload) {
        this.commandPayload = commandPayload;
    }

    public Integer getCommandNumber() {
        return commandNumber;
    }

    public void setCommandNumber(Integer commandNumber) {
        this.commandNumber = commandNumber;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}



