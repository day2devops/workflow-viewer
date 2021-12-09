package com.entarch.workflow;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    public AWSStepFunctions client() {
        return AWSStepFunctionsClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withClientConfiguration(new ClientConfiguration())
                .build();
    }


}
