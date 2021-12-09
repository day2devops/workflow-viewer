package com.entarch.workflow.service;

import com.entarch.workflow.model.WorkflowExecutionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StepFunctionServiceTest {

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    @Autowired
    private StepFunctionsService service;

    @Test
    public void testStepFunction() {
        List<WorkflowExecutionStatus> executions = service.getWorkflowStatus(stateMachineArn);
        assertThat(executions).isNotEmpty();
    }
}
