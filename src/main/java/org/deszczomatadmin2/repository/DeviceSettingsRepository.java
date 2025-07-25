package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.DeviceSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceSettingsRepository extends JpaRepository<DeviceSettings, Long>{
    Optional<DeviceSettings> findByDeviceId(String deviceId);
}