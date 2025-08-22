package com.kt.ems.web;

import com.kt.ems.domain.Measurement;
import com.kt.ems.dto.MeasurementDto;
import com.kt.ems.service.MeasurementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {
    private final MeasurementService measurementService;
    
    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }
    
    @PostMapping
    public Measurement create(@Valid @RequestBody MeasurementDto dto) {
        return measurementService.create(dto);
    }
    
    @GetMapping
    public List<Measurement> getBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var start = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        var end = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return measurementService.getBetween(start, end);
    }
}