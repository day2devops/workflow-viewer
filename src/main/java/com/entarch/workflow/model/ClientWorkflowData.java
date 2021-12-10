package com.entarch.workflow.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ClientWorkflowData {

    private String owner;
    private String email;
    private String name;
    private String status;
    private String type;
    private String executionArn;
    private String currentStepName;
    private String pipelineAge;
    private String lastActionTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime currentStepEnteredTime;

}
