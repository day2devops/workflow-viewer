package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.ClientData;
import com.entarch.workflow.model.ClientWorkflowData;
import com.entarch.workflow.model.ClientWorkflowStatus;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.entarch.workflow.service.ClientDataService;
import com.entarch.workflow.service.StepFunctionsService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Route(value = "/workflow", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Pipeline | Prospect Conversion Tracker")
public class WorkflowView extends VerticalLayout {

    private static final String owner = "mishimaltd@gmail.com";

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    private final Grid<ClientWorkflowStatus> grid = new Grid<>(ClientWorkflowStatus.class, false);

    public WorkflowView() {

        add(createDashboard());

        grid.addColumn(createClientRenderer()).setHeader("Prospect").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn("currentStepName").setHeader("Prospect Stage");
        grid.addColumn((ValueProvider<ClientWorkflowStatus, String>)
                clientWorkflowStatus -> clientWorkflowStatus.getStartTime()
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))).setHeader("Started")
                .setSortable(true);
        grid.addColumn((ValueProvider<ClientWorkflowStatus, String>)
                clientWorkflowStatus -> clientWorkflowStatus.getCurrentStepEnteredTime()
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))).setHeader("Last Activity")
                .setSortable(true);
        grid.addColumn(createStatusRenderer()).setHeader("Workflow Status").setAutoWidth(true).setFlexGrow(0);
        add(grid);
        setSizeFull();
        grid.setItems(getStatusList());
    }

    public static List<ClientWorkflowStatus> getStatusList() {
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
        return statusList;
    }

    private static TemplateRenderer<ClientWorkflowStatus> createClientRenderer() {
        return TemplateRenderer.<ClientWorkflowStatus>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span><b> [[item.name]] </b></span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ([[item.email]])" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", ClientWorkflowStatus::getName)
                .withProperty("email", ClientWorkflowStatus::getEmail);
    }

    private static TemplateRenderer<ClientWorkflowStatus> createStatusRenderer() {
        return TemplateRenderer.<ClientWorkflowStatus>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span class=\"[[ item.status ]]\"><b> [[item.status]] </b></span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("status", ClientWorkflowStatus::getStatus);
    }

    private static HorizontalLayout createDashboard() {
        List<ClientWorkflowStatus> statusList = getStatusList();
        long converted = statusList.stream().filter(clientWorkflowStatus ->
                "SUCCEEDED".equals(clientWorkflowStatus.getStatus())).count();
        long inprogress = statusList.stream().filter(clientWorkflowStatus ->
                "RUNNING".equals(clientWorkflowStatus.getStatus())).count();
        long passed = statusList.stream().filter(clientWorkflowStatus ->
                "FAILED".equals(clientWorkflowStatus.getStatus())).count();


        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.add(createStatus("Converted", converted, "green"));
        layout.add(createStatus("In Progress", inprogress, "blue"));
        layout.add(createStatus("Passed", passed, "orange"));
        return layout;
    }

    private static VerticalLayout createStatus(String title, long count, String color) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight("150px");
        verticalLayout.setWidth("150px");
        verticalLayout.setClassName("dashboard-component");
        verticalLayout.setClassName("dashboard-" + color);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Div number = new Div();
        Span numberSpan = new Span("" + count);
        numberSpan.setClassName("dashboard-number");
        number.add(numberSpan);
        Div detail = new Div();
        Span detailSpan = new Span(title);
        detailSpan.setClassName("dashboard-title");
        detail.add(detailSpan);
        verticalLayout.add(numberSpan);
        verticalLayout.add(detailSpan);
        verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, numberSpan, detailSpan);
        return verticalLayout;
    }


}
