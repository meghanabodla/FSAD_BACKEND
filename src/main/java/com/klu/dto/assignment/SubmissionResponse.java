package com.klu.dto.assignment;

import java.time.LocalDateTime;
import java.util.List;

public class SubmissionResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String textAnswer;
    private List<String> mcqAnswers;
    private Integer grade;
    private String remarks;
    private boolean graded;
    private LocalDateTime submittedAt;
    private String uploadedFileName;
    private boolean canModify;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public List<String> getMcqAnswers() {
        return mcqAnswers;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public boolean isGraded() {
        return graded;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public static class Builder {
        private final SubmissionResponse instance = new SubmissionResponse();

        public Builder id(Long id) {
            instance.id = id;
            return this;
        }

        public Builder studentId(Long studentId) {
            instance.studentId = studentId;
            return this;
        }

        public Builder studentName(String studentName) {
            instance.studentName = studentName;
            return this;
        }

        public Builder studentEmail(String studentEmail) {
            instance.studentEmail = studentEmail;
            return this;
        }

        public Builder textAnswer(String textAnswer) {
            instance.textAnswer = textAnswer;
            return this;
        }

        public Builder mcqAnswers(List<String> mcqAnswers) {
            instance.mcqAnswers = mcqAnswers;
            return this;
        }

        public Builder grade(Integer grade) {
            instance.grade = grade;
            return this;
        }

        public Builder remarks(String remarks) {
            instance.remarks = remarks;
            return this;
        }

        public Builder graded(boolean graded) {
            instance.graded = graded;
            return this;
        }

        public Builder submittedAt(LocalDateTime submittedAt) {
            instance.submittedAt = submittedAt;
            return this;
        }

        public Builder uploadedFileName(String uploadedFileName) {
            instance.uploadedFileName = uploadedFileName;
            return this;
        }

        public Builder canModify(boolean canModify) {
            instance.canModify = canModify;
            return this;
        }

        public SubmissionResponse build() {
            return instance;
        }
    }
}
