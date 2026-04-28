package com.klu.dto.assignment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.klu.model.AssignmentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignmentRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private AssignmentType type;

    @NotNull
    private LocalDateTime deadline;

    private Integer durationMinutes;
    private Integer totalMarks;
    private List<McqQuestionRequest> questions = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssignmentType getType() {
        return type;
    }

    public void setType(AssignmentType type) {
        this.type = type;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public List<McqQuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<McqQuestionRequest> questions) {
        this.questions = questions;
    }
}
