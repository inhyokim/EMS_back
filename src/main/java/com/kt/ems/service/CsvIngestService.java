package com.kt.ems.service;

import com.kt.ems.domain.*;
import com.kt.ems.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class CsvIngestService {

    private final UploadBatchRepository batchRepo;
    private final UploadErrorRepository errorRepo;
    private final SensorRepository sensorRepo;
    private final LocationRepository locationRepo;
    private final MeasurementRepository measurementRepo;

    public CsvIngestService(UploadBatchRepository batchRepo, UploadErrorRepository errorRepo,
                           SensorRepository sensorRepo, LocationRepository locationRepo,
                           MeasurementRepository measurementRepo) {
        this.batchRepo = batchRepo;
        this.errorRepo = errorRepo;
        this.sensorRepo = sensorRepo;
        this.locationRepo = locationRepo;
        this.measurementRepo = measurementRepo;
    }

    public record UploadResult(Long batchId, int totalRows, int validRows, int invalidRows, List<Map<String, Object>> errors) {}

    /**
     * Expected headers: building_name, zone_name, meter_no, timestamp, value
     * We map building+zone -> Location (upsert by name "building/zone"), meter_no -> Sensor.sensorName (upsert).
     * timestamp must be ISO-8601; value is decimal >= 0.
     */
    @Transactional
    public UploadResult ingestCsv(String filename, InputStream in) throws IOException {
        UploadBatch batch = UploadBatch.builder()
                .fileName(filename)
                .uploadedAt(Instant.now())
                .totalRows(0).validRows(0).invalidRows(0)
                .build();
        batch = batchRepo.save(batch);

        List<Map<String,Object>> errList = new ArrayList<>();

        try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             var parser = CSVParser.parse(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())) {

            int total = 0, ok = 0, bad = 0;
            for (CSVRecord r : parser) {
                total++;
                int rowNum = (int) r.getRecordNumber();
                try {
                    String building = required(r, "building_name");
                    String zone     = required(r, "zone_name");
                    String meterNo  = required(r, "meter_no");
                    String tsStr    = required(r, "timestamp");
                    String valStr   = required(r, "value");

                    // Parse
                    Instant ts;
                    try {
                        ts = OffsetDateTime.parse(tsStr).toInstant(); // ISO-8601 expected
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("timestamp must be ISO-8601");
                    }
                    BigDecimal val = new BigDecimal(valStr);
                    if (val.signum() < 0) throw new IllegalArgumentException("value must be >= 0");

                    // Upsert Location (building/zone)
                    String locName = building + "/" + zone;
                    Location loc = locationRepo.findAll().stream()
                            .filter(l -> locName.equals(l.getName()))
                            .findFirst()
                            .orElseGet(() -> locationRepo.save(Location.builder().name(locName).build()));

                    // Upsert Sensor by meter_no
                    Sensor sensor = sensorRepo.findAll().stream()
                            .filter(s -> meterNo.equals(s.getSensorName()))
                            .findFirst()
                            .orElseGet(() -> sensorRepo.save(Sensor.builder()
                                    .sensorName(meterNo).type("POWER").location(loc).build()));

                    // Save measurement
                    measurementRepo.save(Measurement.builder()
                            .sensor(sensor).value(val).measuredAt(ts).build());

                    ok++;
                } catch (Exception e) {
                    bad++;
                    String raw = r.toString();
                    var ue = UploadError.builder()
                            .batch(batch)
                            .rowNumber(rowNum)
                            .errorMessage(e.getMessage())
                            .rawLine(raw)
                            .build();
                    errorRepo.save(ue);
                    errList.add(Map.of("row", rowNum, "error", e.getMessage()));
                }
            }
            batch.setTotalRows(total);
            batch.setValidRows(ok);
            batch.setInvalidRows(bad);
            batchRepo.save(batch);
        }
        return new UploadResult(batch.getId(), batch.getTotalRows(), batch.getValidRows(), batch.getInvalidRows(), errList);
    }

    private static String required(CSVRecord r, String key){
        String v = r.get(key);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("missing: "+key);
        return v;
    }

    @Transactional
    public void deleteBatch(Long batchId){
        // Delete errors and (optionally) related measurements: for demo, we only delete error rows + batch record.
        errorRepo.deleteByBatchId(batchId);
        batchRepo.deleteById(batchId);
        // NOTE: If you want to roll back inserted measurements by batch, add a batch_id on measurement and delete by it.
    }
}
