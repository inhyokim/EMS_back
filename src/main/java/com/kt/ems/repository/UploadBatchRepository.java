package com.kt.ems.repository;

import com.kt.ems.domain.UploadBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadBatchRepository extends JpaRepository<UploadBatch, Long> {
}
