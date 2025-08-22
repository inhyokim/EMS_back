package com.kt.ems.dto;

import jakarta.validation.constraints.*;

public record SensorDto(
    @NotBlank String sensorName,
    @NotBlank String type,
    @NotNull Long locationId
) {}