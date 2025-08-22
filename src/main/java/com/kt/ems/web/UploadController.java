package com.kt.ems.web;

import com.kt.ems.service.CsvIngestService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/readings")
public class UploadController {

    private final CsvIngestService csvIngestService;

    public UploadController(CsvIngestService csvIngestService) {
        this.csvIngestService = csvIngestService;
    }

    // CREATE (Upload + Validate + Save)  → CSV 업로드 및 검증/저장
    @PostMapping(path="/upload", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file) {
        try {
            var res = csvIngestService.ingestCsv(file.getOriginalFilename(), file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "upload_failed", "message", e.getMessage()));
        }
    }

    // DELETE → 업로드 배치 삭제 (오류 리포트 포함 레코드 삭제)
    @DeleteMapping("/uploads/{batchId}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long batchId){
        csvIngestService.deleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }
}
