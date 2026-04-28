package com.klu.dto.assignment;

import java.time.LocalDateTime;
import java.util.List;

import com.klu.model.AssignmentStatus;
import com.klu.model.AssignmentType;

public class AssignmentSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private AssignmentType type;
    private AssignmentStatus status;
    private LocalDateTime deadline;
    private Integer durationMinutes;
    private Integer totalMarks;
    private String attachmentName;
    private String teacherName;
    private List<QuestionResponse> questions;
    private SubmissionResponse mySubmission;
    private Integer submissionCount;
    private Integer pendingCount;
    private Integer gradedCount;
    private Integer ungradedCount;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public AssignmentType getType() {
        return type;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public SubmissionResponse getMySubmission() {
        return mySubmission;
    }

    public Integer getSubmissionCount() {
        return submissionCount;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public Integer getGradedCount() {
        return gradedCount;
    }

    public Integer getUngradedCount() {
        return ungradedCount;
    }

    public static class Builder {
        private final AssignmentSummaryResponse instance = new AssignmentSummaryResponse();

        public Builder id(Long id) {
            instance.id = id;
            return this;
        }

        public Builder title(String title) {
            instance.title = title;
            return this;
        }

        public Builder description(String description) {
            instance.description = description;
            return this;
        }

        public Builder type(AssignmentType type) {
            instance.type = type;
            return this;
        }

        public Builder status(AssignmentStatus status) {
            instance.status = status;
            return this;
        }

        public Builder deadline(LocalDateTime deadline) {
            instance.deadline = deadline;
            return this;
        }

        public Builder durationMinutes(Integer durationMinutes) {
            instance.durationMinutes = durationMinutes;
            return this;
        }

        public Builder totalMarks(Integer totalMarks) {
            instance.totalMarks = totalMarks;
            return this;
        }

        public Builder attachmentName(String attachmentName) {
            instance.attachmentName = attachmentName;
            return this;
        }

        public Builder teacherName(String teacherName) {
            instance.teacherName = teacherName;
            return this;
        }

        public Builder questions(List<QuestionResponse> questions) {
            instance.questions = questions;
            return this;
        }

        public Builder mySubmission(SubmissionResponse mySubmission) {
            instance.mySubmission = mySubmission;
            return this;
        }

        public Builder submissionCount(Integer submissionCount) {
            instance.submissionCount = submissionCount;
            return this;
        }

        public Builder pendingCount(Integer pendingCount) {
            instance.pendingCount = pendingCount;
            return this;
        }

        public Builder gradedCount(Integer gradedCount) {
            instance.gradedCount = gradedCount;
            return this;
        }

        public Builder ungradedCount(Integer ungradedCount) {
            instance.ungradedCount = ungradedCount;
            return this;
        }

        public AssignmentSummaryResponse build() {
            return instance;
        }
    }
}
