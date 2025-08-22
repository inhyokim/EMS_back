package com.kt.ems.service;

import com.kt.ems.domain.*;
import com.kt.ems.dto.SensorDto;
import com.kt.ems.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class SensorService {
    private final SensorRepository sensorRepository;
    private final LocationRepository locationRepository;
    
    public SensorService(SensorRepository sensorRepository, LocationRepository locationRepository) {
        this.sensorRepository = sensorRepository;
        this.locationRepository = locationRepository;
    }
    
    public Sensor create(SensorDto dto) {
        var loc = locationRepository.findById(dto.locationId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid locationId"));
        return sensorRepository.save(Sensor.builder()
            .sensorName(dto.sensorName())
            .type(dto.type())
            .location(loc)
            .build());
    }
    
    @Transactional(readOnly=true)
    public Sensor get(Long id) {
        return sensorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));
    }
    
    public Sensor update(Long id, SensorDto dto) {
        var s = get(id);
        if (!s.getLocation().getId().equals(dto.locationId())) {
            s.setLocation(locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid locationId")));
        }
        s.setSensorName(dto.sensorName());
        s.setType(dto.type());
        return s;
    }
    
    public void delete(Long id) {
        sensorRepository.deleteById(id);
    }
    
    @Transactional(readOnly=true)
    public List<Sensor> list() {
        return sensorRepository.findAll();
    }
}