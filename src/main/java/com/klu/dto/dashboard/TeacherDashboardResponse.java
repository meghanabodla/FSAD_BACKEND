package com.klu.dto.dashboard;

import java.util.List;

import com.klu.dto.assignment.AssignmentSummaryResponse;

public class TeacherDashboardResponse {
    private long totalAssignments;
    private long activeAssignments;
    private long closedAssignments;
    private long totalSubmissions;
    private List<AssignmentSummaryResponse> assignments;

    public static Builder builder() {
        return new Builder();
    }

    public long getTotalAssignments() {
        return totalAssignments;
    }

    public long getActiveAssignments() {
        return activeAssignments;
    }

    public long getClosedAssignments() {
        return closedAssignments;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public List<AssignmentSummaryResponse> getAssignments() {
        return assignments;
    }

    public static class Builder {
        private final TeacherDashboardResponse instance = new TeacherDashboardResponse();

        public Builder totalAssignments(long totalAssignments) {
            instance.totalAssignments = totalAssignments;
            return this;
        }

        public Builder activeAssignments(long activeAssignments) {
            instance.activeAssignments = activeAssignments;
            return this;
        }

        public Builder closedAssignments(long closedAssignments) {
            instance.closedAssignments = closedAssignments;
            return this;
        }

        public Builder totalSubmissions(long totalSubmissions) {
            instance.totalSubmissions = totalSubmissions;
            return this;
        }

        public Builder assignments(List<AssignmentSummaryResponse> assignments) {
            instance.assignments = assignments;
            return this;
        }

        public TeacherDashboardResponse build() {
            return instance;
        }
    }
}
