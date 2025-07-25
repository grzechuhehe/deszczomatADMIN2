package org.deszczomatadmin2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceUploadRequest {
    private String username;
    private String password;
    private Long deviceId;
    private String payload;

}
