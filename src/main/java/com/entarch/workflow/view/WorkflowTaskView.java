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
@PageTitle("Tasks | Workflow")
public class WorkflowTaskView extends VerticalLayout {

    private final Grid<WorkflowTask> grid = new Grid<>(WorkflowTask.class, false);

    public WorkflowTaskView() {
        grid.addColumn("client").setAutoWidth(true);
        grid.addColumn("task").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.addComponentColumn((ValueProvider<WorkflowTask, Component>) workflowTask -> {
            if("Open".equals(workflowTask.getStatus())) {
                Button button = new Button();
                button.setText("Mark Done");
                button.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE));
                if(workflowTask.getTask().equals("DocuSign")) {
                    button.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        String uuid = workflowTask.getUuid();
                        String redirectUri = "http://localhost:8080/eg001?uuid=" + uuid;
                        ServiceFunction.get(WorkflowTaskService.class).updateWorkflowTaskStatus(uuid, "Complete");
                        UI.getCurrent().getPage().setLocation(redirectUri);
                    });
                } else {
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

        add(grid);
        setSizeFull();
        updateList();
    }

    public void updateList() {
        WorkflowTaskService service = ServiceFunction.get(WorkflowTaskService.class);
        //grid.setItems(service.getWorkflowTasksByOwner("mishimaltd@gmail.com"));
        grid.setItems(service.getAllWorkflowTasks());
    }

}
