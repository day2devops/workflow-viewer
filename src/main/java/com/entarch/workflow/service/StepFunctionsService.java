package com.entarch.workflow.service;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.GetExecutionHistoryRequest;
import com.amazonaws.services.stepfunctions.model.GetExecutionHistoryResult;
import com.amazonaws.services.stepfunctions.model.ListExecutionsRequest;
import com.amazonaws.services.stepfunctions.model.ListExecutionsResult;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
public class StepFunctionsService {

    final Logger logger = LoggerFactory.getLogger(StepFunctionsService.class);

    @Autowired
    private AWSStepFunctions client;

    /**
     * Returns workflow executions for state machine
     *
     * @param stateMachineArn
     * @return
     */
    public List<WorkflowExecutionStatus> getWorkflowStatus(String stateMachineArn) {
        logger.info("Getting execution history for state machine ARN {}", stateMachineArn);
        List<WorkflowExecutionStatus> executionStatusList = new ArrayList<>();
        List<CompletableFuture<GetExecutionHistoryResult>> futures = new ArrayList<>();

        ListExecutionsResult result = client.listExecutions(new ListExecutionsRequest()
                .withStateMachineArn(stateMachineArn));

        result.getExecutions().forEach(item -> {
            GetExecutionHistoryRequest request = new GetExecutionHistoryRequest()
                    .withExecutionArn(item.getExecutionArn())
                    .withReverseOrder(true);
            futures.add(CompletableFuture.completedFuture(client.getExecutionHistory(request))
                    .whenComplete((result1, throwable) -> result1.getEvents().stream().filter(historyEvent ->
                                    "TaskStateEntered".equals(historyEvent.getType())).findFirst()
                            .ifPresent(historyEvent -> executionStatusList.add(
                                    new WorkflowExecutionStatus(item.getExecutionArn(),
                                            item.getStatus(),
                                            historyEvent.getStateEnteredEventDetails().getName())))));
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return executionStatusList;
    }

}
