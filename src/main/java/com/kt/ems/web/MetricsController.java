package com.kt.ems.web;

import com.kt.ems.service.MeasurementService;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final MeasurementService measurementService;
    
    public MetricsController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }
    
    @GetMapping("/daily-average")
    public List<Map<String,Object>> dailyAverage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var start = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        var end = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return measurementService.dailyAvg(start, end);
    }
}