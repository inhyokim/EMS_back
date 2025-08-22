package com.kt.ems.web;

import com.kt.ems.repository.MeasurementRepository;
import com.kt.ems.service.ExternalWeatherService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/ext")
public class ExternalController {

    private final ExternalWeatherService weatherService;
    private final MeasurementRepository measurementRepository;

    public ExternalController(ExternalWeatherService weatherService, MeasurementRepository measurementRepository) {
        this.weatherService = weatherService;
        this.measurementRepository = measurementRepository;
    }

    /**
     * 기상(현재) + 최근 24시간 사용량 합계를 함께 제공
     */
    @GetMapping("/weather-usage")
    public Map<String,Object> weatherUsage(
            @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        var weather = weatherService.currentSeoul();

        var to = (toDate==null? LocalDate.now(ZoneOffset.UTC): toDate).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var from = to.minus(Duration.ofHours(24));
        // 최근 24시간 합계 (간단히 value 합)
        var list = measurementRepository.findWithSensorAndLocationBetween(from, to);
        var total = list.stream().map(m -> m.getValue()).reduce((a,b)->a.add(b)).orElse(java.math.BigDecimal.ZERO);

        return Map.of(
                "weather", weather,
                "usageLast24h", total
        );
    }
}
