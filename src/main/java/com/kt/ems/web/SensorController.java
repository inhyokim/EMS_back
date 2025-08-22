package com.kt.ems.web;

import com.kt.ems.domain.Sensor;
import com.kt.ems.dto.SensorDto;
import com.kt.ems.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;
    
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @PostMapping
    public ResponseEntity<Sensor> create(@Valid @RequestBody SensorDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.create(dto));
    }
    
    @GetMapping("/{id}")
    public Sensor get(@PathVariable Long id) {
        return sensorService.get(id);
    }
    
    @PutMapping("/{id}")
    public Sensor update(@PathVariable Long id, @Valid @RequestBody SensorDto dto) {
        return sensorService.update(id, dto);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sensorService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public List<Sensor> list() {
        return sensorService.list();
    }
}