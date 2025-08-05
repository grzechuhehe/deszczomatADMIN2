package org.deszczomatadmin2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryRequest {
    @JsonProperty("did")
    public String deviceId;           // device.deviceId
    public String sta;          // status
    public Float spd_c;         // currentSpeed
    public Float spd_d;         // desiredSpeed
    public Float dis;           // distance
    public Integer toe;         // timeOfEnd (offset)
    public Integer tte;         // timeToEnd
    public Float bat;           // akuVoltage
    public Float wds;           // windSpeed
    public String wdd;           // windDirection (degrees)
    public Float ps;            // pressure
    public Float alt;           // not used, can be logged
}
