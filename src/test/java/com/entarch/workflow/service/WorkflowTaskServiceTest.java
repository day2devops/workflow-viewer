package com.entarch.workflow.service;

import com.entarch.workflow.model.WorkflowTask;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WorkflowTaskServiceTest {

    @Autowired
    private WorkflowTaskService service;

    private final Faker faker = new Faker(new Random());

    @Test
    //@Disabled("Run manually")
    public void testBulkAddTasks() {
        int count = 1;
        for(int i = 0; i < count; i++ ) {
            String email = faker.bothify("????##@gmail.com");
            service.createWorkflowTask(buildWorkflowTask(email));
        }
    }

    @Test
    public void testGetWorkflowTasks() {
        assertThat(service.getAllWorkflowTasks()).isNotEmpty();
    }

    private WorkflowTask buildWorkflowTask(String email) {
        return WorkflowTask.builder()
                .uuid(UUID.randomUUID().toString())
                .token(faker.crypto().sha512())
                .email(email)
                .client(faker.bothify("????##@gmail.com"))
                .task(faker.color().name())
                .status("Open")
                .build();
    }
}
