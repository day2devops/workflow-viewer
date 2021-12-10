package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.ClientData;
import com.entarch.workflow.model.ClientWorkflowData;
import com.entarch.workflow.model.WorkflowExecutionStatus;
import com.entarch.workflow.service.ClientDataService;
import com.entarch.workflow.service.StepFunctionsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Prospects | Prospect Conversion Tracker")
public class ClientDataView extends VerticalLayout {

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:CustomerOnboardingV2";

    private final Grid<ClientWorkflowData> grid = new Grid<>(ClientWorkflowData.class, false);

    public ClientDataView() {

        grid.addColumn(createClientRenderer()).setHeader("Prospect").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(createPipelineRenderer()).setHeader("Pipeline Stage").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(createPipelineStatusRenderer()).setHeader("Pipeline Status").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn("type").setHeader("Client Type").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn("owner").setAutoWidth(true).setHeader("Pipeline Owner").setSortable(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        GridContextMenu<ClientWorkflowData> contextMenu = grid.addContextMenu();
        Button take = new Button();
        take.setText("Start Onboarding");
        take.setIcon(new Icon(VaadinIcon.FLIGHT_TAKEOFF));
        contextMenu.addItem(take, event -> {
            String email = event.getItem().get().getEmail();
            StepFunctionsService stepFunctionsService = ServiceFunction.get(StepFunctionsService.class);
            String executionArn = stepFunctionsService.initiateWorkflowExecution(stateMachineArn,
                    Map.of("email", event.getItem().get().getEmail(),
                            "owner", "mishimaltd@gmail.com"));
            ClientDataService clientDataService = ServiceFunction.get(ClientDataService.class);
            clientDataService.setClientDataOwnerAndType(email,"mishimaltd@gmail.com", "OnBoarding");
            clientDataService.setExecutionArn(email, executionArn);
            UI.getCurrent().getPage().reload();
        });

        Button returnButton = new Button();
        returnButton.setText("Revert Onboarding");
        returnButton.setIcon(new Icon(VaadinIcon.FLIGHT_LANDING));

        contextMenu.addItem(returnButton, event -> {
            String email = event.getItem().get().getEmail();
            ClientDataService service = ServiceFunction.get(ClientDataService.class);
            service.setClientDataOwnerAndType(email,"None", "Prospect");
            service.setExecutionArn(email, "None");
            UI.getCurrent().getPage().reload();
        });

        add(grid);
        setSizeFull();
        updateList();
    }

    public void updateList() {
        StepFunctionsService service = ServiceFunction.get(StepFunctionsService.class);
        ClientDataService clientDataService = ServiceFunction.get(ClientDataService.class);

        List<ClientData> clientData = clientDataService.getAllClientData();
        Map<String,WorkflowExecutionStatus> status = service.getWorkflowStatus(stateMachineArn).stream()
                .collect(Collectors.toMap(WorkflowExecutionStatus::getExecutionArn, Function.identity()));

        List<ClientWorkflowData> clientDataList = new ArrayList<>();
        clientData.forEach(client -> {
            WorkflowExecutionStatus workflowExecutionStatus = status.get(client.getExecutionArn());
            clientDataList.add(ClientWorkflowData.builder()
                    .owner(client.getOwner())
                    .email(client.getEmail())
                    .name(client.getName())
                    .type(client.getType())
                    .executionArn(client.getExecutionArn())
                    .status(workflowExecutionStatus != null? "(Workflow status: " + workflowExecutionStatus.getStatus() + ")": null)
                    .currentStepName(workflowExecutionStatus != null? workflowExecutionStatus.getCurrentStepName(): null)
                    .startTime(workflowExecutionStatus != null? workflowExecutionStatus.getStartTime(): null)
                    .endTime(workflowExecutionStatus != null? workflowExecutionStatus.getEndTime(): null)
                    .currentStepEnteredTime(workflowExecutionStatus != null? workflowExecutionStatus.getCurrentStepEnteredTime(): null)
                            .pipelineAge(workflowExecutionStatus != null? buildPipelineAge(workflowExecutionStatus): null)
                    .lastActionTime(workflowExecutionStatus != null? buildLastActionTime(workflowExecutionStatus): null)
                    .build()
            );
        });

        grid.setItems(clientDataList);

    }

    private static TemplateRenderer<ClientWorkflowData> createClientRenderer() {
        return TemplateRenderer.<ClientWorkflowData>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span><b> [[item.name]] </b></span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ([[item.email]])" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", ClientWorkflowData::getName)
                .withProperty("email", ClientWorkflowData::getEmail);
    }


    private static TemplateRenderer<ClientWorkflowData> createPipelineRenderer() {
        return TemplateRenderer.<ClientWorkflowData>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span><b> [[item.currentStepName]] </b></span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      <i>[[item.status]]</i>" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("currentStepName", ClientWorkflowData::getCurrentStepName)
                .withProperty("status", ClientWorkflowData::getStatus);
    }

    private static TemplateRenderer<ClientWorkflowData> createPipelineStatusRenderer() {
        return TemplateRenderer.<ClientWorkflowData>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> [[item.lastActionTime]] </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "     <i> [[item.pipelineAge]]" + "   </i> </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pipelineAge", ClientWorkflowData::getPipelineAge)
                .withProperty("lastActionTime", ClientWorkflowData::getLastActionTime);
    }

    private static String buildPipelineAge(WorkflowExecutionStatus workflowExecutionStatus) {
        if(workflowExecutionStatus.getStartTime() == null) return null;
        else {
            Duration age = Duration.between(workflowExecutionStatus.getStartTime(), LocalDateTime.now());
            long hours = age.toHours();
            long days = age.toDays();
            if( days == 0L) {
                if( hours == 0) {
                    return "(Pipeline age: " + age.toMinutes() + " minutes)";
                } else {
                    return "(Pipeline age: " + hours + " hours)";
                }
            } else {
                hours = hours - days * 24;
                if(hours == 0 || days > 3) {
                    return "(Pipeline age: " + days + " days)";
                } else {
                    return "(Pipeline age: " + days + " days " + hours + " hours)";
                }
            }
        }
    }

    private static String buildLastActionTime(WorkflowExecutionStatus workflowExecutionStatus) {
        if(workflowExecutionStatus.getCurrentStepName() == null) return null;
        else {
            String lastAction = workflowExecutionStatus.getCurrentStepEnteredTime().format(
                    DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
            return "Last Activity: " + lastAction;
        }
    }

}
