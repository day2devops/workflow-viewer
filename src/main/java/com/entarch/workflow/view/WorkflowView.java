package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.ClientData;
import com.entarch.workflow.model.ClientWorkflowStatus;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.entarch.workflow.service.ClientDataService;
import com.entarch.workflow.service.StepFunctionsService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "/workflow", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Tracking | Workflow")
public class WorkflowView extends VerticalLayout {

    private static final String owner = "mishimaltd@gmail.com";

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    private final Grid<ClientWorkflowStatus> grid = new Grid<>(ClientWorkflowStatus.class);

    public WorkflowView() {
        grid.setColumns("name", "email", "status", "currentStepName", "startTime", "currentStepEnteredTime", "endTime");
        add(grid);
        setSizeFull();
        updateList();
    }

    public void updateList() {
        StepFunctionsService service = ServiceFunction.get(StepFunctionsService.class);
        ClientDataService clientDataService = ServiceFunction.get(ClientDataService.class);

        Map<String,ClientData> myClientData = clientDataService.getClientDataByOwner(owner).stream()
                .collect(Collectors.toMap(ClientData::getExecutionArn, Function.identity()));
        List<WorkflowExecutionStatus> status = service.getWorkflowStatus(stateMachineArn).stream()
                        .filter(workflowExecutionStatus ->
                                myClientData.containsKey(workflowExecutionStatus.getExecutionArn()))
                .collect(Collectors.toList());

        List<ClientWorkflowStatus> statusList = new ArrayList<>();
        status.forEach(workflowExecutionStatus -> {
            ClientData clientData = myClientData.get(workflowExecutionStatus.getExecutionArn());
            statusList.add(ClientWorkflowStatus.builder()
                    .owner(owner)
                    .email(clientData.getEmail())
                    .name(clientData.getName())
                    .status(workflowExecutionStatus.getStatus())
                    .currentStepName(workflowExecutionStatus.getCurrentStepName())
                    .startTime(workflowExecutionStatus.getStartTime())
                    .endTime(workflowExecutionStatus.getEndTime())
                    .currentStepEnteredTime(workflowExecutionStatus.getCurrentStepEnteredTime())
                    .build()
            );
        });

        grid.setItems(statusList);
    }




}
