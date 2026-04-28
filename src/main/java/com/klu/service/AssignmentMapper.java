package com.klu.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klu.dto.assignment.AssignmentSummaryResponse;
import com.klu.dto.assignment.QuestionResponse;
import com.klu.dto.assignment.SubmissionResponse;
import com.klu.model.Assignment;
import com.klu.model.McqQuestion;
import com.klu.model.Submission;

@Component
public class AssignmentMapper {

    private final ObjectMapper objectMapper;

    public AssignmentMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AssignmentSummaryResponse toSummary(Assignment assignment, Submission mySubmission, Integer pendingCount) {
        boolean canModify = mySubmission != null
            && !mySubmission.isGraded()
            && assignment.getType() != com.klu.model.AssignmentType.MCQ;
        int gradedCount = (int) assignment.getSubmissions().stream().filter(Submission::isGraded).count();
        int ungradedCount = assignment.getSubmissions().size() - gradedCount;

        return AssignmentSummaryResponse.builder()
            .id(assignment.getId())
            .title(assignment.getTitle())
            .description(assignment.getDescription())
            .type(assignment.getType())
            .status(assignment.getStatus())
            .deadline(assignment.getDeadline())
            .durationMinutes(assignment.getDurationMinutes())
            .totalMarks(assignment.getTotalMarks())
            .attachmentName(assignment.getAttachmentName())
            .teacherName(assignment.getTeacher().getName())
            .questions(assignment.getQuestions().stream().map(this::toQuestionResponse).toList())
            .mySubmission(mySubmission == null ? null : toSubmissionResponse(mySubmission, canModify))
            .submissionCount(assignment.getSubmissions().size())
            .pendingCount(pendingCount)
            .gradedCount(gradedCount)
            .ungradedCount(ungradedCount)
            .build();
    }

    public SubmissionResponse toSubmissionResponse(Submission submission, boolean canModify) {
        return SubmissionResponse.builder()
            .id(submission.getId())
            .studentId(submission.getStudent().getId())
            .studentName(submission.getStudent().getName())
            .studentEmail(submission.getStudent().getEmail())
            .textAnswer(submission.getTextAnswer())
            .mcqAnswers(readList(submission.getMcqAnswersJson()))
            .grade(submission.getGrade())
            .remarks(submission.getRemarks())
            .graded(submission.isGraded())
            .submittedAt(submission.getSubmittedAt())
            .uploadedFileName(submission.getUploadedFileName())
            .canModify(canModify)
            .build();
    }

    public String writeList(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize list", ex);
        }
    }

    public List<String> readList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to deserialize list", ex);
        }
    }

    private QuestionResponse toQuestionResponse(McqQuestion question) {
        return QuestionResponse.builder()
            .id(question.getId())
            .questionText(question.getQuestionText())
            .options(readList(question.getOptionsJson()))
            .marks(question.getMarks())
            .build();
    }
}
