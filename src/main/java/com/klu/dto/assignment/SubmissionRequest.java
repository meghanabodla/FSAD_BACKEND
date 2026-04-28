package com.klu.dto.assignment;

import java.util.ArrayList;
import java.util.List;

public class SubmissionRequest {
    private String textAnswer;
    private List<String> mcqAnswers = new ArrayList<>();

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public List<String> getMcqAnswers() {
        return mcqAnswers;
    }

    public void setMcqAnswers(List<String> mcqAnswers) {
        this.mcqAnswers = mcqAnswers;
    }
}
