package com.entarch.workflow.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WorkflowTask {

    private String uuid;
    private String token;
    private String email;
    private String client;
    private String task;
    private String status;

}
