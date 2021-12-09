package com.entarch.workflow.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.entarch.workflow.model.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class ClientDataService {

    private static final String tableName = "ClientData";

    @Autowired
    private AmazonDynamoDB dynamoDB;

    public void createClientData(ClientData clientData) {
        PutItemRequest request = new PutItemRequest()
                .withTableName(tableName)
                .withItem(Map.of(
                        "email", new AttributeValue(clientData.getEmail()),
                        "name", new AttributeValue(clientData.getName()),
                        "owner", new AttributeValue(clientData.getOwner()),
                        "type", new AttributeValue(clientData.getType()),
                        "executionArn", new AttributeValue(clientData.getExecutionArn())
                ));
        dynamoDB.putItem(request);
    }

    public void setClientDataOwnerAndType(String email, String owner, String type) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("owner", new AttributeValueUpdate(new AttributeValue(owner), AttributeAction.PUT));
        updates.put("type", new AttributeValueUpdate(new AttributeValue(type), AttributeAction.PUT));
        dynamoDB.updateItem(tableName,
                Map.of("email", new AttributeValue(email)),
                updates);
    }

    public void setExecutionArn(String email, String executionArn) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("executionArn", new AttributeValueUpdate(new AttributeValue(executionArn), AttributeAction.PUT));
        dynamoDB.updateItem(tableName,
                Map.of("email", new AttributeValue(email)),
                updates);
    }

    public List<ClientData> getAllClientData() {
        ScanResult scanResult = dynamoDB.scan(tableName, List.of("email", "name", "owner", "type", "executionArn"));
        return scanResult.getItems().stream().map(this::build).collect(Collectors.toList());
    }

    public List<ClientData> getClientDataByOwner(String owner) {
        return getAllClientData().stream().filter(clientData ->
                owner.equals(clientData.getOwner()))
                .collect(Collectors.toList());
    }

    public void deleteClientData(String email) {
        DeleteItemRequest request = new DeleteItemRequest()
                .withTableName(tableName)
                .withKey(Map.of("email", new AttributeValue(email)));
        dynamoDB.deleteItem(request);
    }

    public Optional<ClientData> getClientData(String email) {
        GetItemResult result = dynamoDB.getItem(tableName, Map.of("email", new AttributeValue(email)));
        if(result.getItem() == null) {
            return Optional.empty();
        } else {
            return Optional.of(build(result.getItem()));
        }
    }

    private ClientData build(Map<String,AttributeValue> map) {
        return ClientData.builder()
                .email(map.get("email").getS())
                .name(map.get("name").getS())
                .owner(map.get("owner").getS())
                .type(map.get("type").getS())
                .executionArn(map.get("executionArn").getS())
                .build();
    }

}
