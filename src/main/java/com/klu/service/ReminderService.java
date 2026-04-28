package com.klu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
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
    private final EmailService emailService;

    public ReminderService(
        AssignmentRepository assignmentRepository,
        UserRepository userRepository,
        SubmissionRepository submissionRepository,
        DeadlineReminderRepository deadlineReminderRepository,
        EmailService emailService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.deadlineReminderRepository = deadlineReminderRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 1800000)
    public void sendDeadlineReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);
        List<Assignment> assignments = assignmentRepository.findByDeadlineBetween(now, next24Hours);
        List<User> students = userRepository.findByRole(Role.STUDENT);

        for (Assignment assignment : assignments) {
            if (assignment.getStatus() != AssignmentStatus.ACTIVE) {
                continue;
            }

            for (User student : students) {
                boolean submitted = submissionRepository.findByAssignmentAndStudent(assignment, student).isPresent();
                boolean reminded = deadlineReminderRepository.findByAssignmentAndStudent(assignment, student).isPresent();
                if (!submitted && !reminded) {
                    emailService.sendEmail(
                        student.getEmail(),
                        "Assignment deadline reminder",
                        "Reminder: " + assignment.getTitle() + " is due on " + assignment.getDeadline() + "."
                    );
                    DeadlineReminder reminder = new DeadlineReminder();
                    reminder.setAssignment(assignment);
                    reminder.setStudent(student);
                    deadlineReminderRepository.save(reminder);
                }
            }
        }
    }
}
