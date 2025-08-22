package com.kt.ems.repository;

import com.kt.ems.domain.UploadError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadErrorRepository extends JpaRepository<UploadError, Long> {
    void deleteByBatchId(Long batchId);
}
