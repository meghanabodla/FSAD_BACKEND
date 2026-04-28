package com.klu.dto.dashboard;

import java.util.List;

import com.klu.dto.assignment.AssignmentSummaryResponse;

public class StudentDashboardResponse {
    private long totalAttempts;
    private double averageScore;
    private int highestScore;
    private int lowestScore;
    private List<AssignmentSummaryResponse> assignments;
    private List<AssignmentSummaryResponse> upcomingDeadlines;

    public static Builder builder() {
        return new Builder();
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public int getLowestScore() {
        return lowestScore;
    }

    public List<AssignmentSummaryResponse> getAssignments() {
        return assignments;
    }

    public List<AssignmentSummaryResponse> getUpcomingDeadlines() {
        return upcomingDeadlines;
    }

    public static class Builder {
        private final StudentDashboardResponse instance = new StudentDashboardResponse();

        public Builder totalAttempts(long totalAttempts) {
            instance.totalAttempts = totalAttempts;
            return this;
        }

        public Builder averageScore(double averageScore) {
            instance.averageScore = averageScore;
            return this;
        }

        public Builder highestScore(int highestScore) {
            instance.highestScore = highestScore;
            return this;
        }

        public Builder lowestScore(int lowestScore) {
            instance.lowestScore = lowestScore;
            return this;
        }

        public Builder assignments(List<AssignmentSummaryResponse> assignments) {
            instance.assignments = assignments;
            return this;
        }

        public Builder upcomingDeadlines(List<AssignmentSummaryResponse> upcomingDeadlines) {
            instance.upcomingDeadlines = upcomingDeadlines;
            return this;
        }

        public StudentDashboardResponse build() {
            return instance;
        }
    }
}
