package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandRepository extends JpaRepository<Command, Long>{
    List<Command> findByDeviceIdAndExecutedFalse(String deviceId);
}
