package com.kt.ems.repository;

import com.kt.ems.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor,Long> {
}