package com.kt.ems.web;

import com.kt.ems.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // READ (weekly/monthly JSON)
    @GetMapping("/summary")
    public List<Map<String,Object>> summary(
            @RequestParam String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ){
        var start = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        var end = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return switch (period.toLowerCase()) {
            case "weekly" -> reportService.weekly(start, end);
            case "monthly" -> reportService.monthly(start, end);
            default -> throw new IllegalArgumentException("period must be weekly|monthly");
        };
    }

    // READ (CSV download)
    @GetMapping(value="/summary.csv", produces="text/csv")
    public ResponseEntity<byte[]> summaryCsv(
            @RequestParam String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ){
        var data = summary(period, from, to);
        String csv = "bucket,usage\n" + data.stream()
                .map(m -> m.get("bucket")+","+m.get("usage"))
                .collect(Collectors.joining("\n"));
        var bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-"+period+".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
