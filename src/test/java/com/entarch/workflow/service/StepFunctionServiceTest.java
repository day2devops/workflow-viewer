package com.entarch.workflow.service;

import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StepFunctionServiceTest {

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    private final Faker faker = new Faker(new Random());

    @Autowired
    private StepFunctionsService service;

    @Test
    public void testGetExecutionHistory() {
        List<WorkflowExecutionStatus> executions = service.getWorkflowStatus(stateMachineArn);
        assertThat(executions).isNotEmpty();
    }

    @Test
    @Disabled
    public void testCreateExecution() {
        Map<String,Object> metadata = Map.of(
                "FirstName", faker.name().firstName(),
                "LastName", faker.name().lastName(),
                "Email", faker.bothify("????##@gmail.com")
        );
        String executionArn = service.initiateWorkflowExecution(stateMachineArn, metadata);
        assertThat(executionArn).isNotNull();
    }
}
