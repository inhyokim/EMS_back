package com.kt.ems.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MeasurementDto(
    @NotNull Long sensorId,
    @NotNull @DecimalMin("0") BigDecimal value,
    String measuredAt
) {}