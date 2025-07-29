package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandRepository extends JpaRepository<Command, Long>{
    //TODO DESIRED SPEED, TRYB PRACY[STRING],
    List<Command> findByDeviceIdAndExecutedFalse(Long deviceId);
}
