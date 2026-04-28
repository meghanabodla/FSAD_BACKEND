package com.klu.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.klu.dto.assignment.AssignmentRequest;
import com.klu.dto.assignment.AssignmentSummaryResponse;
import com.klu.dto.assignment.McqQuestionRequest;
import com.klu.dto.dashboard.StudentDashboardResponse;
import com.klu.dto.dashboard.TeacherDashboardResponse;
import com.klu.model.Assignment;
import com.klu.model.AssignmentStatus;
import com.klu.model.AssignmentType;
import com.klu.model.McqQuestion;
import com.klu.model.Role;
import com.klu.model.Submission;
import com.klu.model.User;
import com.klu.repository.AssignmentRepository;
import com.klu.repository.SubmissionRepository;
import com.klu.repository.UserRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AssignmentMapper assignmentMapper;

    public AssignmentService(
        AssignmentRepository assignmentRepository,
        SubmissionRepository submissionRepository,
        UserRepository userRepository,
        FileStorageService fileStorageService,
        AssignmentMapper assignmentMapper
    ) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.assignmentMapper = assignmentMapper;
    }

    @Transactional
    public AssignmentSummaryResponse createAssignment(User teacher, AssignmentRequest request, MultipartFile file) {
        Assignment assignment = new Assignment();
        applyAssignmentData(assignment, request, file);
        assignment.setTeacher(teacher);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
        return assignmentMapper.toSummary(assignment, null, studentCount() - assignment.getSubmissions().size());
    }

    @Transactional
    public AssignmentSummaryResponse updateAssignment(Long id, User teacher, AssignmentRequest request, MultipartFile file) {
        Assignment assignment = getTeacherAssignment(id, teacher);
        if (!assignment.getSubmissions().isEmpty()) {
            throw new IllegalStateException("Submitted assignments cannot be edited");
        }
        applyAssignmentData(assignment, request, file);
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
        return assignmentMapper.toSummary(assignment, null, studentCount() - assignment.getSubmissions().size());
    }

    @Transactional
    public TeacherDashboardResponse teacherDashboard(User teacher) {
        List<Assignment> assignments = assignmentRepository.findByTeacherOrderByCreatedAtDesc(teacher);
        refreshStatuses(assignments);
        List<AssignmentSummaryResponse> summaries = assignments.stream()
            .map(assignment -> assignmentMapper.toSummary(assignment, null, studentCount() - assignment.getSubmissions().size()))
            .toList();

        long totalSubmissions = assignments.stream().mapToLong(a -> a.getSubmissions().size()).sum();
        return TeacherDashboardResponse.builder()
            .totalAssignments(assignments.size())
            .activeAssignments(assignments.stream().filter(a -> a.getStatus() == AssignmentStatus.ACTIVE).count())
            .closedAssignments(assignments.stream().filter(a -> a.getStatus() == AssignmentStatus.CLOSED).count())
            .totalSubmissions(totalSubmissions)
            .assignments(summaries)
            .build();
    }

    @Transactional
    public StudentDashboardResponse studentDashboard(User student) {
        List<Assignment> assignments = assignmentRepository.findAll().stream()
            .sorted(Comparator.comparing(Assignment::getDeadline))
            .toList();
        refreshStatuses(assignments);

        List<Submission> submissions = submissionRepository.findByStudent(student);
        List<Integer> grades = submissions.stream()
            .filter(Submission::isGraded)
            .map(Submission::getGrade)
            .filter(value -> value != null)
            .toList();

        List<AssignmentSummaryResponse> summaries = assignments.stream()
            .map(assignment -> {
                Submission mySubmission = submissions.stream()
                    .filter(submission -> submission.getAssignment().getId().equals(assignment.getId()))
                    .findFirst()
                    .orElse(null);
                return assignmentMapper.toSummary(assignment, mySubmission, null);
            })
            .toList();

        LocalDateTime now = LocalDateTime.now();
        List<AssignmentSummaryResponse> upcoming = assignments.stream()
            .filter(a -> a.getDeadline().isAfter(now) && a.getDeadline().isBefore(now.plusHours(24)))
            .filter(a -> submissions.stream().noneMatch(s -> s.getAssignment().getId().equals(a.getId())))
            .map(a -> assignmentMapper.toSummary(a, null, null))
            .toList();

        return StudentDashboardResponse.builder()
            .totalAttempts(submissions.size())
            .averageScore(grades.isEmpty() ? 0 : grades.stream().mapToInt(Integer::intValue).average().orElse(0))
            .highestScore(grades.isEmpty() ? 0 : grades.stream().mapToInt(Integer::intValue).max().orElse(0))
            .lowestScore(grades.isEmpty() ? 0 : grades.stream().mapToInt(Integer::intValue).min().orElse(0))
            .assignments(summaries)
            .upcomingDeadlines(upcoming)
            .build();
    }

    @Transactional(readOnly = true)
    public Assignment getAssignment(Long id) {
        return assignmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
    }

    @Transactional(readOnly = true)
    public Assignment getTeacherAssignment(Long id, User teacher) {
        Assignment assignment = getAssignment(id);
        if (!assignment.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalStateException("You cannot access another teacher's assignment");
        }
        return assignment;
    }

    @Transactional
    public void deleteAssignment(Long id, User teacher) {
        Assignment assignment = getTeacherAssignment(id, teacher);
        if (!assignment.getSubmissions().isEmpty()) {
            throw new IllegalStateException("Cannot delete an assignment with submissions");
        }
        assignmentRepository.delete(assignment);
    }

    private void applyAssignmentData(Assignment assignment, AssignmentRequest request, MultipartFile file) {
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setType(request.getType());
        assignment.setDeadline(request.getDeadline());
        assignment.setDurationMinutes(request.getType() == AssignmentType.MCQ ? request.getDurationMinutes() : null);
        assignment.setTotalMarks(request.getTotalMarks());
        assignment.setStatus(request.getDeadline().isBefore(LocalDateTime.now()) ? AssignmentStatus.CLOSED : AssignmentStatus.ACTIVE);

        if (file != null && !file.isEmpty()) {
            assignment.setAttachmentPath(fileStorageService.store(file, "assignment-files"));
            assignment.setAttachmentName(file.getOriginalFilename());
        }

        assignment.getQuestions().clear();
        if (request.getType() == AssignmentType.MCQ) {
            if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
                throw new IllegalArgumentException("MCQ assignment requires questions");
            }
            for (McqQuestionRequest questionRequest : request.getQuestions()) {
                McqQuestion question = new McqQuestion();
                question.setAssignment(assignment);
                question.setQuestionText(questionRequest.getQuestionText());
                question.setOptionsJson(assignmentMapper.writeList(questionRequest.getOptions()));
                question.setCorrectAnswer(questionRequest.getCorrectAnswer());
                question.setMarks(questionRequest.getMarks());
                assignment.getQuestions().add(question);
            }
        }
    }

    private void refreshStatuses(List<Assignment> assignments) {
        assignments.forEach(assignment -> {
            if (assignment.getStatus() == AssignmentStatus.ACTIVE && assignment.getDeadline().isBefore(LocalDateTime.now())) {
                assignment.setStatus(AssignmentStatus.CLOSED);
                assignmentRepository.save(assignment);
            }
        });
    }

    private int studentCount() {
        return userRepository.findByRole(Role.STUDENT).size();
    }
}
