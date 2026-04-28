package com.klu.dto.assignment;

import java.util.List;

public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<String> options;
    private Integer marks;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public Integer getMarks() {
        return marks;
    }

    public static class Builder {
        private final QuestionResponse instance = new QuestionResponse();

        public Builder id(Long id) {
            instance.id = id;
            return this;
        }

        public Builder questionText(String questionText) {
            instance.questionText = questionText;
            return this;
        }

        public Builder options(List<String> options) {
            instance.options = options;
            return this;
        }

        public Builder marks(Integer marks) {
            instance.marks = marks;
            return this;
        }

        public QuestionResponse build() {
            return instance;
        }
    }
}
