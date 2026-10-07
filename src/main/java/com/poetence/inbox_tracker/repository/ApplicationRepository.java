package com.poetence.inbox_tracker.repository;

import com.poetence.inbox_tracker.model.Application;
import com.poetence.inbox_tracker.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStatus(ApplicationStatus status);
}