package com.klu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.klu.dto.assignment.GradeRequest;
import com.klu.dto.assignment.SubmissionRequest;
import com.klu.dto.assignment.SubmissionResponse;
import com.klu.model.Assignment;
import com.klu.model.AssignmentStatus;
import com.klu.model.AssignmentType;
import com.klu.model.Submission;
import com.klu.model.User;
import com.klu.repository.SubmissionRepository;

@Service
public class SubmissionService {

    private final AssignmentService assignmentService;
    private final SubmissionRepository submissionRepository;
    private final FileStorageService fileStorageService;
    private final AssignmentMapper assignmentMapper;

    public SubmissionService(
        AssignmentService assignmentService,
        SubmissionRepository submissionRepository,
        FileStorageService fileStorageService,
        AssignmentMapper assignmentMapper
    ) {
        this.assignmentService = assignmentService;
        this.submissionRepository = submissionRepository;
        this.fileStorageService = fileStorageService;
        this.assignmentMapper = assignmentMapper;
    }

    @Transactional
    public SubmissionResponse submit(Long assignmentId, User student, SubmissionRequest request, MultipartFile file) {
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        if (assignment.getStatus() == AssignmentStatus.CLOSED || assignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Assignment deadline has passed");
        }

        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student).orElseGet(Submission::new);
        if (submission.getId() != null && submission.isGraded()) {
            throw new IllegalStateException("Graded submission cannot be modified");
        }

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setGraded(false);
        submission.setGrade(null);
        submission.setRemarks(null);

        if (assignment.getType() == AssignmentType.TEXT) {
            submission.setTextAnswer(request.getTextAnswer());
            submission.setMcqAnswersJson(null);
            submission.setUploadedFileName(null);
            submission.setUploadedFilePath(null);
        } else if (assignment.getType() == AssignmentType.MCQ) {
            List<String> answers = request.getMcqAnswers();
            submission.setMcqAnswersJson(assignmentMapper.writeList(answers));
            submission.setTextAnswer(null);
            submission.setUploadedFileName(null);
            submission.setUploadedFilePath(null);

            int score = 0;
            for (int index = 0; index < assignment.getQuestions().size(); index++) {
                String given = index < answers.size() ? answers.get(index) : null;
                if (assignment.getQuestions().get(index).getCorrectAnswer().equals(given)) {
                    score += assignment.getQuestions().get(index).getMarks();
                }
            }
            submission.setGrade(score);
            submission.setRemarks("Auto-graded MCQ submission");
            submission.setGraded(true);
        } else {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File submission is required");
            }
            submission.setUploadedFilePath(fileStorageService.store(file, "student-submissions"));
            submission.setUploadedFileName(file.getOriginalFilename());
            submission.setTextAnswer(null);
            submission.setMcqAnswersJson(null);
        }

        submissionRepository.save(submission);
        return assignmentMapper.toSubmissionResponse(submission, !submission.isGraded() && assignment.getType() != AssignmentType.MCQ);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getTeacherSubmissions(Long assignmentId, User teacher) {
        Assignment assignment = assignmentService.getTeacherAssignment(assignmentId, teacher);
        return submissionRepository.findByAssignment(assignment).stream()
            .map(submission -> assignmentMapper.toSubmissionResponse(submission, false))
            .toList();
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getStudentSubmission(Long assignmentId, User student) {
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        boolean canModify = !submission.isGraded() && assignment.getType() != AssignmentType.MCQ;
        return assignmentMapper.toSubmissionResponse(submission, canModify);
    }

    @Transactional
    public SubmissionResponse grade(Long submissionId, User teacher, GradeRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        assignmentService.getTeacherAssignment(submission.getAssignment().getId(), teacher);
        submission.setGrade(request.getGrade());
        submission.setRemarks(request.getRemarks());
        submission.setGraded(true);
        submissionRepository.save(submission);
        return assignmentMapper.toSubmissionResponse(submission, false);
    }

    @Transactional
    public void deleteStudentSubmission(Long assignmentId, User student) {
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        if (submission.isGraded() || assignment.getType() == AssignmentType.MCQ) {
            throw new IllegalStateException("This submission cannot be deleted");
        }
        submissionRepository.delete(submission);
    }

    @Transactional(readOnly = true)
    public String getSubmissionFilePath(Long submissionId, User requester) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        boolean ownSubmission = submission.getStudent().getId().equals(requester.getId());
        boolean teacherAccess = submission.getAssignment().getTeacher().getId().equals(requester.getId());
        if (!ownSubmission && !teacherAccess) {
            throw new IllegalStateException("Unauthorized file access");
        }
        return submission.getUploadedFilePath();
    }

    @Transactional(readOnly = true)
    public String getAssignmentAttachmentPath(Long assignmentId, User requester) {
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        boolean teacherAccess = assignment.getTeacher().getId().equals(requester.getId());
        boolean studentAccess = requester.getRole().name().equals("STUDENT");
        if (!teacherAccess && !studentAccess) {
            throw new IllegalStateException("Unauthorized file access");
        }
        return assignment.getAttachmentPath();
    }
}
