package com.kt.ems.domain;

import jakarta.persistence.*;

@Entity
@Table(name="upload_error")
public class UploadError {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="batch_id",nullable=false)
    private UploadBatch batch;

    @Column(name="row_number",nullable=false)
    private int rowNumber;
    
    @Column(name="error_message",nullable=false,length=500)
    private String errorMessage;
    
    @Lob
    @Column(name="raw_line")
    private String rawLine;

    public UploadError() {}

    public UploadError(Long id, UploadBatch batch, int rowNumber, String errorMessage, String rawLine) {
        this.id = id;
        this.batch = batch;
        this.rowNumber = rowNumber;
        this.errorMessage = errorMessage;
        this.rawLine = rawLine;
    }

    public static UploadErrorBuilder builder() {
        return new UploadErrorBuilder();
    }

    public static class UploadErrorBuilder {
        private Long id;
        private UploadBatch batch;
        private int rowNumber;
        private String errorMessage;
        private String rawLine;

        public UploadErrorBuilder id(Long id) { this.id = id; return this; }
        public UploadErrorBuilder batch(UploadBatch batch) { this.batch = batch; return this; }
        public UploadErrorBuilder rowNumber(int rowNumber) { this.rowNumber = rowNumber; return this; }
        public UploadErrorBuilder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public UploadErrorBuilder rawLine(String rawLine) { this.rawLine = rawLine; return this; }

        public UploadError build() {
            return new UploadError(id, batch, rowNumber, errorMessage, rawLine);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UploadBatch getBatch() { return batch; }
    public void setBatch(UploadBatch batch) { this.batch = batch; }
    
    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getRawLine() { return rawLine; }
    public void setRawLine(String rawLine) { this.rawLine = rawLine; }
}
