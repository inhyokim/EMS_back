package com.kt.ems.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="location")
public class Location {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable=false,length=100)
    private String name;
    
    @Column(length=255)
    private String description;
    
    public Location() {}
    
    public Location(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public static LocationBuilder builder() { return new LocationBuilder(); }
    
    public static class LocationBuilder {
        private Long id;
        private String name;
        private String description;
        
        public LocationBuilder id(Long id) { this.id = id; return this; }
        public LocationBuilder name(String name) { this.name = name; return this; }
        public LocationBuilder description(String description) { this.description = description; return this; }
        public Location build() { return new Location(id, name, description); }
    }
}