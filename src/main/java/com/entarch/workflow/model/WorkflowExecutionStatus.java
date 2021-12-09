package com.entarch.workflow.model;

public record WorkflowExecutionStatus(String executionArn, String status, String currentStepName) {
}
