package com.entarch.workflow.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientData {

    private String email;
    private String name;
    private String owner;
    private String type;
    private String executionArn;

}
