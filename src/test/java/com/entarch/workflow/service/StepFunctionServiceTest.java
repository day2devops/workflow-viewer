package com.entarch.workflow.service;

import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
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

    @EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches=".*")
    @Test
    public void testGetExecutionHistory() {
        List<WorkflowExecutionStatus> executions = service.getWorkflowStatus(stateMachineArn);
        assertThat(executions).isNotEmpty();
    }

    @Test
    @Disabled
    public void testSendTaskSuccess() {
        String token = "AAAAKgAAAAIAAAAAAAAAAjWfYxL3lSutWifHY7d2XaHf8sAXsFnX97YICPRBCAtQfC79LNEM4bq9wVjP0yN7ULZF86JWDt3aznXOZtQ9tmqo4LuJzkpe4qwHesNmGEErvSN1vUXgZb6S2WmiwpAHbo4bMTFVNc1r1OPQKIUKbFvzFDkPLillZYEvsozmoMH19Wou8kph6csq+chyFAl1EvinoCf3vUCMHd4fgyQOrH/W9EzWw6oltnLoIm72VQ82HLXQRLiUqljSLLzptNXtkIvFwKOljg7CLHpOAD+p0BY3sguLEtOnjCz8gBLoCSUfXYhssJYqFKms75IHVi+I21dRKTsPuCIhkKDm2iwowRsSRQLbEkxqduV9zK/NBSEko5B+6/XGLJC3+kGYrQvO3F4zZq74PfQOMp9fC16fENLc4z1qV1XsjRNXQkfOaaobjXkqiljdQjJy/uJLWalJxVkqNuFbiQg/RUdbr2OEvml+yGFMmemgBxxunDBT2xHoxExsGtcZ9dkkWE167vysJm6cOj55g2zz2jdrrtN4UhpJ/J9VzfUDO+UMc7jOzoI3bWE1/39bGISpN8gEYmoXcHYxn1HTsi4L5x2CXGgf79OsSzc8VrqD3R66yBmHc+YM";
        service.sendTaskSuccess(token);

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
