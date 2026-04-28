package com.klu.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.klu.dto.assignment.AssignmentRequest;
import com.klu.dto.assignment.AssignmentSummaryResponse;
import com.klu.dto.assignment.GradeRequest;
import com.klu.dto.assignment.SubmissionResponse;
import com.klu.dto.dashboard.TeacherDashboardResponse;
import com.klu.service.AssignmentService;
import com.klu.service.SubmissionService;
import com.klu.service.UserService;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    public TeacherController(AssignmentService assignmentService, SubmissionService submissionService, UserService userService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public TeacherDashboardResponse dashboard(Authentication authentication) {
        return assignmentService.teacherDashboard(userService.getByEmail(authentication.getName()));
    }

    @PostMapping(value = "/assignments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentSummaryResponse createAssignment(
        Authentication authentication,
        @RequestPart("metadata") AssignmentRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return assignmentService.createAssignment(userService.getByEmail(authentication.getName()), request, file);
    }

    @PutMapping(value = "/assignments/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentSummaryResponse updateAssignment(
        @PathVariable Long id,
        Authentication authentication,
        @RequestPart("metadata") AssignmentRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return assignmentService.updateAssignment(id, userService.getByEmail(authentication.getName()), request, file);
    }

    @DeleteMapping("/assignments/{id}")
    public void deleteAssignment(@PathVariable Long id, Authentication authentication) {
        assignmentService.deleteAssignment(id, userService.getByEmail(authentication.getName()));
    }

    @GetMapping("/assignments/{id}/submissions")
    public List<SubmissionResponse> submissions(@PathVariable Long id, Authentication authentication) {
        return submissionService.getTeacherSubmissions(id, userService.getByEmail(authentication.getName()));
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public SubmissionResponse gradeSubmission(
        @PathVariable Long submissionId,
        Authentication authentication,
        @RequestBody GradeRequest request
    ) {
        return submissionService.grade(submissionId, userService.getByEmail(authentication.getName()), request);
    }
}
