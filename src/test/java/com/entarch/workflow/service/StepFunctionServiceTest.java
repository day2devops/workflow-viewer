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

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:CustomerOnboardingV2";

    private final Faker faker = new Faker(new Random());

    @Autowired
    private StepFunctionsService service;

    @Test
    public void testGetExecutionHistory() {
        List<WorkflowExecutionStatus> executions = service.getWorkflowStatus(stateMachineArn);
        assertThat(executions).isNotEmpty();
    }

    @Test
    //@Disabled
    public void testSendTaskSuccess() {
        String token = "AAAAKgAAAAIAAAAAAAAAAtTKsuQAR4jKLE+kJUleuRojfPW6c/WFmsCIlxmoHMOo2rJ3+kz+BZhQ9B7ar0a2E61ONbz2cKq2VfaP1yg3AmVYelR/TxtEwfVmt+k/kP7bisRm4MiI5V4MMOlmqDlNuIHHAi99LzGx9GdwwoMVfvE9f1gFLSoeJD1uNwS3EL7RZ+Z2GxUKpygJUm7+fUOi0AFWFsITvxM8+FABBrBQ76SdwHYfYoNx/aDoe+SMijP2UnVK8c+TunFJ8E8Vz4zk+Gd3fAUiXnX/BP2WH5orbPFprg+7mkcbzSIRin/tl0Zo2gIKlQiCfnC/ls8V8F2Zr4mB68qayVO65wvXC918eJuASSWs7UJwHP/3aH2vvA6bwOYNdG3MAUV6wv7OhZ/3Mn4K9rt/QKIW4QhtesEdhDEBxfoQU5VnJ6hsmzxu0bMELeFwUqG5Czlva2E436Hah28nwmK4iAcaFpDBCTLmo3E8HWEyzGIZNISRXVkwermEKfvSktbjA6lObholCOzEG7xVzuJEZqkbeOG/X2TtTRIE8QZKTu91X1Qrn+4rvV5/u1+ar9xYDPlMDpjkGzXw4V+8+3esh2vBCMi5uQyWEbLznjoGVORiy9L8NR3hIW+R";
        //service.sendTaskSuccess(token);

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
