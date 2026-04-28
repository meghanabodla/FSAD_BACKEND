package com.klu.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.model.Assignment;
import com.klu.model.AssignmentStatus;
import com.klu.model.User;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTeacherOrderByCreatedAtDesc(User teacher);
    List<Assignment> findByStatusOrderByDeadlineAsc(AssignmentStatus status);
    List<Assignment> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);
}
