package com.kt.ems.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="measurement",indexes=@Index(name="idx_measurement_ts",columnList="measured_at"))
public class Measurement {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch=FetchType.EAGER,optional=false)
    @JoinColumn(name="sensor_id",nullable=false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sensor sensor;
    
    @NotNull
    @Column(name="\"value\"",nullable=false,precision=18,scale=6)
    private BigDecimal value;
    
    @NotNull
    @Column(name="measured_at",nullable=false)
    private Instant measuredAt;
    
    public Measurement() {}
    
    public Measurement(Long id, Sensor sensor, BigDecimal value, Instant measuredAt) {
        this.id = id;
        this.sensor = sensor;
        this.value = value;
        this.measuredAt = measuredAt;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
    
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    
    public Instant getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(Instant measuredAt) { this.measuredAt = measuredAt; }
    
    public static MeasurementBuilder builder() { return new MeasurementBuilder(); }
    
    public static class MeasurementBuilder {
        private Long id;
        private Sensor sensor;
        private BigDecimal value;
        private Instant measuredAt;
        
        public MeasurementBuilder id(Long id) { this.id = id; return this; }
        public MeasurementBuilder sensor(Sensor sensor) { this.sensor = sensor; return this; }
        public MeasurementBuilder value(BigDecimal value) { this.value = value; return this; }
        public MeasurementBuilder measuredAt(Instant measuredAt) { this.measuredAt = measuredAt; return this; }
        public Measurement build() { return new Measurement(id, sensor, value, measuredAt); }
    }
}