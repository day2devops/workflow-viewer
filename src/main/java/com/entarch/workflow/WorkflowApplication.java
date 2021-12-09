package com.entarch.workflow;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.vaadin.flow.component.dependency.NpmPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class WorkflowApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    public AWSStepFunctions stepFunctionsClient() {
        return AWSStepFunctionsClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withClientConfiguration(new ClientConfiguration())
                .build();
    }

    @Bean
    public AmazonDynamoDB dynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .build();

    }



}
