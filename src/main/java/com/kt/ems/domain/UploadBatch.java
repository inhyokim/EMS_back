package com.kt.ems.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="upload_batch")
public class UploadBatch {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="file_name",nullable=false)
    private String fileName;
    
    @Column(name="uploaded_at",nullable=false)
    private Instant uploadedAt;
    
    @Column(name="total_rows",nullable=false)
    private int totalRows;
    
    @Column(name="valid_rows",nullable=false)
    private int validRows;
    
    @Column(name="invalid_rows",nullable=false)
    private int invalidRows;

    public UploadBatch() {}

    public UploadBatch(Long id, String fileName, Instant uploadedAt, int totalRows, int validRows, int invalidRows) {
        this.id = id;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.totalRows = totalRows;
        this.validRows = validRows;
        this.invalidRows = invalidRows;
    }

    public static UploadBatchBuilder builder() {
        return new UploadBatchBuilder();
    }

    public static class UploadBatchBuilder {
        private Long id;
        private String fileName;
        private Instant uploadedAt;
        private int totalRows;
        private int validRows;
        private int invalidRows;

        public UploadBatchBuilder id(Long id) { this.id = id; return this; }
        public UploadBatchBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public UploadBatchBuilder uploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; return this; }
        public UploadBatchBuilder totalRows(int totalRows) { this.totalRows = totalRows; return this; }
        public UploadBatchBuilder validRows(int validRows) { this.validRows = validRows; return this; }
        public UploadBatchBuilder invalidRows(int invalidRows) { this.invalidRows = invalidRows; return this; }

        public UploadBatch build() {
            return new UploadBatch(id, fileName, uploadedAt, totalRows, validRows, invalidRows);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    
    public int getValidRows() { return validRows; }
    public void setValidRows(int validRows) { this.validRows = validRows; }
    
    public int getInvalidRows() { return invalidRows; }
    public void setInvalidRows(int invalidRows) { this.invalidRows = invalidRows; }
}
