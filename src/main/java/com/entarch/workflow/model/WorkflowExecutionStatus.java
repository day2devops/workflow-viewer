package com.entarch.workflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkflowExecutionStatus(String executionArn,
                                      String status,
                                      String currentStepName,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      LocalDateTime currentStepEnteredTime) {
}
