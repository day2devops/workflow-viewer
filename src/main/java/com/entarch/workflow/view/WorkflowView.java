package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.entarch.workflow.service.StepFunctionsService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "/workflow", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Tracking | Workflow")
public class WorkflowView extends VerticalLayout {

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    private final Grid<WorkflowExecutionStatus> grid = new Grid<>(WorkflowExecutionStatus.class);

    public WorkflowView() {
        grid.setColumns("executionArn", "status", "currentStepName", "startTime");
        add(grid);
        setSizeFull();
        updateList();
    }

    public void updateList() {
        StepFunctionsService service = ServiceFunction.get(StepFunctionsService.class);
        grid.setItems(service.getWorkflowStatus(stateMachineArn));
    }




}
