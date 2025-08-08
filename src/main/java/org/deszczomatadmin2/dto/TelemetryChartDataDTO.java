package org.deszczomatadmin2.dto;

import java.time.LocalDateTime;

public class TelemetryChartDataDTO {
    private LocalDateTime timestamp;
    private Object value;

    public TelemetryChartDataDTO(LocalDateTime timestamp, Object value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
