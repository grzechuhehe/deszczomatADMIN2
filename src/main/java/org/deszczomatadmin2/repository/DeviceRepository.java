package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByOwner_Username(String username);
}
