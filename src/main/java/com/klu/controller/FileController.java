package com.klu.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.service.FileStorageService;
import com.klu.service.SubmissionService;
import com.klu.service.UserService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SubmissionService submissionService;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    public FileController(SubmissionService submissionService, FileStorageService fileStorageService, UserService userService) {
        this.submissionService = submissionService;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<Resource> submissionFile(@PathVariable Long submissionId, Authentication authentication) {
        String filePath = submissionService.getSubmissionFilePath(submissionId, userService.getByEmail(authentication.getName()));
        Resource resource = fileStorageService.loadAsResource(filePath);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<Resource> assignmentFile(@PathVariable Long assignmentId, Authentication authentication) {
        String filePath = submissionService.getAssignmentAttachmentPath(assignmentId, userService.getByEmail(authentication.getName()));
        Resource resource = fileStorageService.loadAsResource(filePath);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
