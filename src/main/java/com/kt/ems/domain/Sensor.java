package com.kt.ems.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="sensor")
public class Sensor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable=false,length=100)
    private String sensorName;
    
    @NotBlank
    @Column(nullable=false,length=50)
    private String type;
    
    @NotNull
    @ManyToOne(fetch=FetchType.EAGER,optional=false)
    @JoinColumn(name="location_id",nullable=false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location location;
    
    public Sensor() {}
    
    public Sensor(Long id, String sensorName, String type, Location location) {
        this.id = id;
        this.sensorName = sensorName;
        this.type = type;
        this.location = location;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSensorName() { return sensorName; }
    public void setSensorName(String sensorName) { this.sensorName = sensorName; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public static SensorBuilder builder() { return new SensorBuilder(); }
    
    public static class SensorBuilder {
        private Long id;
        private String sensorName;
        private String type;
        private Location location;
        
        public SensorBuilder id(Long id) { this.id = id; return this; }
        public SensorBuilder sensorName(String sensorName) { this.sensorName = sensorName; return this; }
        public SensorBuilder type(String type) { this.type = type; return this; }
        public SensorBuilder location(Location location) { this.location = location; return this; }
        public Sensor build() { return new Sensor(id, sensorName, type, location); }
    }
}