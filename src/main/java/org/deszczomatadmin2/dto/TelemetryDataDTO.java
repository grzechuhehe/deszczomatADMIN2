package org.deszczomatadmin2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryDataDTO{
    public Long id;
    @JsonProperty("did")
    public String deviceName;           // device.deviceId
    public Integer sta;          // status
    public Integer spd_c;         // currentSpeed
    public Integer spd_d;         // desiredSpeed
    public Integer dis;           // distance
    public Integer toe;         // timeOfEnd (offset)
    public Integer tte;         // timeToEnd
    public Integer bat;           // akuVoltage
    public Integer wds;           // windSpeed
    public Integer wdd;           // windDirection (degrees)
    public Integer ps;            // pressure
    public Integer alt; // not used, can be logged
    public String tmstmp;
}
