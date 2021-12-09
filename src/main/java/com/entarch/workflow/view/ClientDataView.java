package com.entarch.workflow.view;

import com.entarch.workflow.functions.ServiceFunction;
import com.entarch.workflow.model.ClientData;
import com.entarch.workflow.service.ClientDataService;
import com.entarch.workflow.service.StepFunctionsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.util.Map;

@Route(value = "", layout = MainLayout.class)
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Prospects | Workflow")
public class ClientDataView extends VerticalLayout {

    private static final String stateMachineArn = "arn:aws:states:us-east-2:440917644520:stateMachine:EACustomerOnboarding";

    private final Grid<ClientData> grid = new Grid<>(ClientData.class, false);

    public ClientDataView() {

        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn(createOwnerComponentRenderer()).setHeader("Owner")
                .setAutoWidth(true).setSortable(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        GridContextMenu<ClientData> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Take", event -> {
            String email = event.getItem().get().getEmail();
            StepFunctionsService stepFunctionsService = ServiceFunction.get(StepFunctionsService.class);
            String executionArn = stepFunctionsService.initiateWorkflowExecution(stateMachineArn,
                    Map.of("email", event.getItem().get().getEmail()));
            ClientDataService clientDataService = ServiceFunction.get(ClientDataService.class);
            clientDataService.setClientDataOwnerAndType(email,"mishimaltd@gmail.com", "OnBoarding");
            clientDataService.setExecutionArn(email, executionArn);
            UI.getCurrent().getPage().reload();
        });
        contextMenu.addItem("Return", event -> {
            String email = event.getItem().get().getEmail();
            ClientDataService service = ServiceFunction.get(ClientDataService.class);
            service.setClientDataOwnerAndType(email,"None", "Prospect");
            UI.getCurrent().getPage().reload();
        });

        add(grid);
        setSizeFull();
        updateList();
    }

    public void updateList() {
        ClientDataService service = ServiceFunction.get(ClientDataService.class);
        grid.setItems(service.getAllClientData());
    }

    private static final SerializableBiConsumer<Span, ClientData> ownerComponentUpdater = (span, clientData) -> {
        boolean isAvailable = "None".equals(clientData.getOwner());
        String theme = String
                .format("badge %s", isAvailable ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(clientData.getOwner());
    };

    private static ComponentRenderer<Span, ClientData> createOwnerComponentRenderer() {
        return new ComponentRenderer<>(Span::new, ownerComponentUpdater);
    }



}
