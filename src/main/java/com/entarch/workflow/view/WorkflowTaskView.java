package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.WorkflowTask;
import com.entarch.workflow.service.StepFunctionsService;
import com.entarch.workflow.service.WorkflowTaskService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "/tasks", layout = MainLayout.class)
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Dashboard | Prospect Conversion Tracker")
public class WorkflowTaskView extends VerticalLayout {

    private final Grid<WorkflowTask> openTaskGrid = new Grid<>(WorkflowTask.class, false);
    private final Grid<WorkflowTask> closedTaskGrid = new Grid<>(WorkflowTask.class, false);

    public WorkflowTaskView() {
        openTaskGrid.addColumn("client").setAutoWidth(true).setSortable(true);
        openTaskGrid.addColumn("task").setAutoWidth(true).setSortable(true);
        openTaskGrid.addColumn("status").setAutoWidth(true).setSortable(true);
        openTaskGrid.addComponentColumn((ValueProvider<WorkflowTask, Component>) workflowTask -> {
            if("Open".equals(workflowTask.getStatus())) {
                Button button = new Button();
                button.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE));
                if(workflowTask.getTask().equals("Awaiting Customer Signature")) {
                    button.setText("Sign Documents");
                    button.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        String uuid = workflowTask.getUuid();
                        String redirectUri = "http://localhost:8080/eg001?uuid=" + uuid;
                        ServiceFunction.get(WorkflowTaskService.class).updateWorkflowTaskStatus(uuid, "Complete");
                        UI.getCurrent().getPage().setLocation(redirectUri);
                    });
                } else {
                    button.setText("Mark Done");
                    button.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        String token = workflowTask.getToken();
                        ServiceFunction.get(StepFunctionsService.class).sendTaskSuccess(token);
                        ServiceFunction.get(WorkflowTaskService.class).updateWorkflowTaskStatus(token, "Complete");
                        UI.getCurrent().getPage().reload();
                    });
                }
                return button;
            } else {
                return new Span();
            }
        });

        add(new H4("Outstanding Tasks"));

        add(openTaskGrid);

        closedTaskGrid.addColumn("client").setAutoWidth(true);
        closedTaskGrid.addColumn("task").setAutoWidth(true);
        closedTaskGrid.addColumn("status").setAutoWidth(true);

        add(new H4("Completed Tasks"));

        add(closedTaskGrid);

        setSizeFull();
        updateList();
    }

    public void updateList() {
        WorkflowTaskService service = ServiceFunction.get(WorkflowTaskService.class);
        openTaskGrid.setItems(service.getAllWorkflowTasks().stream()
                .filter(workflowTask -> "Open".equals(workflowTask.getStatus())));
        closedTaskGrid.setItems(service.getAllWorkflowTasks().stream()
                .filter(workflowTask -> !"Open".equals(workflowTask.getStatus())));
    }

}
