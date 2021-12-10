package com.entarch.workflow.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Theme(value = Material.class)
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Prospect Conversion Tracker");
        logo.addClassNames("text-l", "m-m");
        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m", "light-blue");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink prospectLink = new RouterLink("Prospects", ClientDataView.class);
        prospectLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink workflowLink = new RouterLink("My Pipeline", WorkflowView.class);
        workflowLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink tasksLink = new RouterLink("My Tasks", WorkflowTaskView.class);
        tasksLink.setHighlightCondition(HighlightConditions.sameLocation());

        VerticalLayout layout = new VerticalLayout(prospectLink, workflowLink, tasksLink);
        layout.addClassNames("background-gray");
        layout.setHeightFull();

        addToDrawer(layout);
    }
}
