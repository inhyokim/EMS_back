package com.kt.ems.service;

import com.kt.ems.repository.MeasurementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class ReportService {
    private final MeasurementRepository measurementRepository;

    public ReportService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> weekly(Instant from, Instant to){
        List<Map<String,Object>> out = new ArrayList<>();
        for (Object[] r : measurementRepository.weeklyUsage(from, to)) {
            out.add(Map.of("bucket", r[0].toString(), "usage", new BigDecimal(r[1].toString())));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> monthly(Instant from, Instant to){
        List<Map<String,Object>> out = new ArrayList<>();
        for (Object[] r : measurementRepository.monthlyUsage(from, to)) {
            out.add(Map.of("bucket", r[0].toString(), "usage", new BigDecimal(r[1].toString())));
        }
        return out;
    }
}
