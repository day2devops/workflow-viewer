package com.entarch.workflow.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(value = Lumo.class)
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Workflow Tracking");
        logo.addClassNames("text-l", "m-m");
        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink prospectLink = new RouterLink("Prospects", ClientDataView.class);
        prospectLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink workflowLink = new RouterLink("Workflow", WorkflowView.class);
        workflowLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink tasksLink = new RouterLink("Tasks", WorkflowTaskView.class);
        tasksLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                prospectLink, workflowLink, tasksLink
        ));
    }
}
