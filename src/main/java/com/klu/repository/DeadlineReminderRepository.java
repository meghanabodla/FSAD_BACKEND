package com.klu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.model.Assignment;
import com.klu.model.DeadlineReminder;
import com.klu.model.User;

public interface DeadlineReminderRepository extends JpaRepository<DeadlineReminder, Long> {
    Optional<DeadlineReminder> findByAssignmentAndStudent(Assignment assignment, User student);
}
