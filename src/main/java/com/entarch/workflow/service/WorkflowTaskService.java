package com.entarch.workflow.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.entarch.workflow.model.WorkflowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class WorkflowTaskService {

    private static final String tableName = "WorkflowTasks";

    @Autowired
    private AmazonDynamoDB dynamoDB;

    public void createWorkflowTask(WorkflowTask workflowTask) {
        PutItemRequest request = new PutItemRequest()
                .withTableName(tableName)
                .withItem(Map.of(
                        "email", new AttributeValue(workflowTask.getEmail()),
                        "client", new AttributeValue(workflowTask.getClient()),
                        "task", new AttributeValue(workflowTask.getTask()),
                        "token", new AttributeValue(workflowTask.getToken()),
                        "uuid", new AttributeValue(workflowTask.getUuid()),
                        "status", new AttributeValue(workflowTask.getStatus())
                ));
        dynamoDB.putItem(request);
    }

    public void updateWorkflowTaskStatus(String uuid, String status) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("status", new AttributeValueUpdate(new AttributeValue(status), AttributeAction.PUT));
        dynamoDB.updateItem(tableName,
                Map.of("uuid", new AttributeValue(uuid)),
                updates);
    }

    public List<WorkflowTask> getAllWorkflowTasks() {
        ScanResult scanResult = dynamoDB.scan(tableName, List.of("uuid", "token", "email", "client", "task", "status"));
        return scanResult.getItems().stream().map(this::build).collect(Collectors.toList());
    }

    public List<WorkflowTask> getWorkflowTasksByOwner(String email) {
        return getAllWorkflowTasks().stream().filter(task ->
                email.equals(task.getEmail())).sorted(Comparator.comparing(WorkflowTask::getStatus))
                .collect(Collectors.toList());
    }

    private WorkflowTask build(Map<String,AttributeValue> map) {
        WorkflowTask.WorkflowTaskBuilder builder = WorkflowTask.builder();
        if( map.containsKey("uuid")) builder.uuid(map.get("uuid").getS());
        if( map.containsKey("token")) builder.token(map.get("token").getS());
        if( map.containsKey("email")) builder.email(map.get("email").getS());
        if( map.containsKey("task")) builder.task(map.get("task").getS());
        if( map.containsKey("client")) builder.client(map.get("client").getS());
        if( map.containsKey("status")) builder.status(map.get("status").getS());
        return builder.build();
    }

}
