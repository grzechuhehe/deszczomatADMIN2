package org.deszczomatadmin2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryDataDTO{
    @JsonProperty("did")
    public Long id;
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

    public String getTmstmp() {
        return tmstmp;
    }

    public TelemetryDataDTO() {
    }

    public TelemetryDataDTO(String tmstmp, Long id,  Integer sta, Integer spd_d, Integer toe, Integer spd_c, Integer dis, Integer tte, Integer wds, Integer bat, Integer wdd, Integer ps, Integer alt) {
        this.tmstmp = tmstmp;
        this.id = id;
        this.sta = sta;
        this.spd_d = spd_d;
        this.toe = toe;
        this.spd_c = spd_c;
        this.dis = dis;
        this.tte = tte;
        this.wds = wds;
        this.bat = bat;
        this.wdd = wdd;
        this.ps = ps;
        this.alt = alt;
    }
}
