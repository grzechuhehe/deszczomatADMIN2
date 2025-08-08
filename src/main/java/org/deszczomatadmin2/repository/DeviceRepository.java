package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByOwner_Username(String username);
    Optional<Device> findByDeviceNameAndOwnerUsername(String deviceName, String username);
    List<Device> findByOwner(User user);

    List<Device> id(Long id);

    Optional<Device> findByIdAndOwnerUsername(Long id, String username);
}
