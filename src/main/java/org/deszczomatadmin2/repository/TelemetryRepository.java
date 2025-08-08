package org.deszczomatadmin2.repository;

import org.deszczomatadmin2.model.Device;
import org.deszczomatadmin2.model.TelemetryData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelemetryRepository extends JpaRepository<TelemetryData, Long>{
    List<TelemetryData> findByDevice(Device device);
    List<TelemetryData> findByDevice_Id(Long id);
}
