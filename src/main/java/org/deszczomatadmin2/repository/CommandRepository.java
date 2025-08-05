package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Command;
import org.deszczomatadmin2.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommandRepository extends JpaRepository<Command, Long> {
    Optional<Command> findFirstByDeviceAndExecutedIsFalseOrderByCreatedAtDesc(Device device);

}
