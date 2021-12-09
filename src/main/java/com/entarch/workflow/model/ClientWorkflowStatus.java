package com.entarch.workflow.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ClientWorkflowStatus {

    private String owner;
    private String email;
    private String name;
    private String status;
    private String currentStepName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime currentStepEnteredTime;

}
