package com.kt.ems.repository;

import com.kt.ems.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor,Long> {
    Optional<Sensor> findBySensorName(String sensorName);
}