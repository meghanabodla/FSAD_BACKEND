package com.klu.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.klu.dto.assignment.SubmissionRequest;
import com.klu.dto.assignment.SubmissionResponse;
import com.klu.dto.dashboard.StudentDashboardResponse;
import com.klu.service.AssignmentService;
import com.klu.service.SubmissionService;
import com.klu.service.UserService;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    public StudentController(AssignmentService assignmentService, SubmissionService submissionService, UserService userService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public StudentDashboardResponse dashboard(Authentication authentication) {
        return assignmentService.studentDashboard(userService.getByEmail(authentication.getName()));
    }

    @GetMapping("/assignments/{id}/submission")
    public SubmissionResponse mySubmission(@PathVariable Long id, Authentication authentication) {
        return submissionService.getStudentSubmission(id, userService.getByEmail(authentication.getName()));
    }

    @PostMapping(value = "/assignments/{id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SubmissionResponse submit(
        @PathVariable Long id,
        Authentication authentication,
        @RequestPart("metadata") SubmissionRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return submissionService.submit(id, userService.getByEmail(authentication.getName()), request, file);
    }

    @DeleteMapping("/assignments/{id}/submission")
    public void deleteSubmission(@PathVariable Long id, Authentication authentication) {
        submissionService.deleteStudentSubmission(id, userService.getByEmail(authentication.getName()));
    }
}
