package com.entarch.workflow.functions;

import com.vaadin.flow.server.VaadinServlet;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ServiceFunction {

    public static <T> T get(Class<T> serviceType) {
        return WebApplicationContextUtils
                .getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
                .getBean(serviceType);
    }
}
