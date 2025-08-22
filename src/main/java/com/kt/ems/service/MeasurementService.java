package com.kt.ems.service;

import com.kt.ems.domain.*;
import com.kt.ems.dto.MeasurementDto;
import com.kt.ems.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final SensorRepository sensorRepository;
    
    public MeasurementService(MeasurementRepository measurementRepository, SensorRepository sensorRepository) {
        this.measurementRepository = measurementRepository;
        this.sensorRepository = sensorRepository;
    }
    
    public Measurement create(MeasurementDto dto) {
        Sensor s = sensorRepository.findById(dto.sensorId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid sensorId"));
        Instant ts = (dto.measuredAt() == null || dto.measuredAt().isBlank()) 
            ? Instant.now() 
            : Instant.parse(dto.measuredAt());
        return measurementRepository.save(Measurement.builder()
            .sensor(s)
            .value(dto.value())
            .measuredAt(ts)
            .build());
    }
    
    @Transactional(readOnly=true)
    public List<Measurement> getBetween(Instant from, Instant to) {
        return measurementRepository.findWithSensorAndLocationBetween(from, to);
    }
    
    @Transactional(readOnly=true)
    public List<Map<String,Object>> dailyAvg(Instant from, Instant to) {
        List<Map<String,Object>> out = new ArrayList<>();
        for (Object[] r : measurementRepository.findDailyAverage(from, to)) {
            out.add(Map.of(
                "sensorId", ((Number)r[0]).longValue(),
                "day", r[1].toString(),
                "avgValue", new BigDecimal(r[2].toString())
            ));
        }
        return out;
    }
}