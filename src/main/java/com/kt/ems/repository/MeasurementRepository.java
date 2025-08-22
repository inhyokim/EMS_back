package com.kt.ems.repository;

import com.kt.ems.domain.Measurement;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement,Long> {
    @Query("select m from Measurement m join fetch m.sensor s join fetch s.location l where m.measuredAt between :from and :to")
    List<Measurement> findWithSensorAndLocationBetween(@Param("from") Instant from, @Param("to") Instant to);
    
    @Query(value = """
        select s.id as sensor_id, m.measured_at as day, avg(m."value") as avg_value
        from measurement m join sensor s on s.id=m.sensor_id
        where m.measured_at between :from and :to
        group by s.id, m.measured_at
        order by s.id, m.measured_at
        """, nativeQuery = true)
    List<Object[]> findDailyAverage(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = """
        select m.measured_at as bucket, sum(m."value") as usage
        from measurement m
        where m.measured_at between :from and :to
        group by m.measured_at
        order by m.measured_at
        """, nativeQuery = true)
    List<Object[]> weeklyUsage(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = """
        select m.measured_at as bucket, sum(m."value") as usage
        from measurement m
        where m.measured_at between :from and :to
        group by m.measured_at
        order by m.measured_at
        """, nativeQuery = true)
    List<Object[]> monthlyUsage(@Param("from") Instant from, @Param("to") Instant to);
}