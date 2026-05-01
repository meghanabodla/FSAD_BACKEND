package com.klu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.klu.model.Assignment;
import com.klu.model.AssignmentStatus;
import com.klu.model.DeadlineReminder;
import com.klu.model.Role;
import com.klu.model.User;
import com.klu.repository.AssignmentRepository;
import com.klu.repository.DeadlineReminderRepository;
import com.klu.repository.SubmissionRepository;
import com.klu.repository.UserRepository;

@Service
public class ReminderService {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final DeadlineReminderRepository deadlineReminderRepository;

    public ReminderService(
        AssignmentRepository assignmentRepository,
        UserRepository userRepository,
        SubmissionRepository submissionRepository,
        DeadlineReminderRepository deadlineReminderRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.deadlineReminderRepository = deadlineReminderRepository;
    }

    public void sendDeadlineReminders() {
        // Email reminders are disabled because mail usage is not required.
    }
}
