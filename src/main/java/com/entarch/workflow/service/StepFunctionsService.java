package com.entarch.workflow.service;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.*;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.entarch.workflow.functions.DateFunction.fromDate;

@Configuration
public class StepFunctionsService {

    private static final Logger logger = LoggerFactory.getLogger(StepFunctionsService.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String,WorkflowExecutionStatus> cache = new HashMap<>();

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    @Autowired
    private AWSStepFunctions client;


    /**
     * Initiates a workflow execution
     */
    public String initiateWorkflowExecution(String stateMachineArn, Map<String,Object> metadata) {
        StartExecutionResult result = client.startExecution(new StartExecutionRequest()
                .withStateMachineArn(stateMachineArn)
                .withInput(gson.toJson(metadata)));
        return result.getExecutionArn();
    }

    public void sendTaskSuccess(String token) {
        SendTaskSuccessRequest request = new SendTaskSuccessRequest().withTaskToken(token);
        client.sendTaskSuccess(request);
    }

    /**
     * Returns workflow executions for state machine
     *
     * @param stateMachineArn
     * @return
     */
    @Timed
    public List<WorkflowExecutionStatus> getWorkflowStatus(String stateMachineArn) {
        logger.info("Started getting execution history for state machine ARN {}", stateMachineArn);
        List<WorkflowExecutionStatus> executionStatusList = new ArrayList<>();
        List<CompletableFuture<GetExecutionHistoryResult>> futures = new ArrayList<>();

        ListExecutionsResult result = client.listExecutions(new ListExecutionsRequest()
                .withStateMachineArn(stateMachineArn));

        // Only process uncached or running processes
        List<ExecutionListItem> toProcess = result.getExecutions().stream().filter(item ->
                cache.get(item.getExecutionArn()) == null || "RUNNING".equals(item.getStatus())).collect(Collectors.toList());

        toProcess.forEach(item -> {
            GetExecutionHistoryRequest request = new GetExecutionHistoryRequest()
                    .withExecutionArn(item.getExecutionArn())
                    .withReverseOrder(true);
            futures.add(CompletableFuture.completedFuture(client.getExecutionHistory(request))
                    .whenComplete((result1, throwable) -> result1.getEvents().stream().filter(historyEvent ->
                                    "TaskStateEntered".equals(historyEvent.getType())).findFirst()
                            .ifPresent(historyEvent -> executionStatusList.add(
                                    WorkflowExecutionStatus.builder()
                                            .executionArn(item.getExecutionArn())
                                            .status(item.getStatus())
                                            .currentStepName(historyEvent.getStateEnteredEventDetails().getName())
                                            .startTime(fromDate(item.getStartDate()))
                                            .endTime(fromDate(item.getStopDate()))
                                            .currentStepEnteredTime(fromDate(historyEvent.getTimestamp()))
                                            .build()
                                    ))));
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executionStatusList.forEach(workflowExecutionStatus ->
                cache.put(workflowExecutionStatus.getExecutionArn(), workflowExecutionStatus));

        logger.info("Finished getting execution history for state machine ARN {}", stateMachineArn);
        return cache.values().stream().toList();
    }




}
