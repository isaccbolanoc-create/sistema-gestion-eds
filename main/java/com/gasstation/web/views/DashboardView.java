package com.gasstation.web.views;

import com.gasstation.web.dao.*;
import com.gasstation.web.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    @Autowired
    public DashboardView(CombustibleDAO combustibleDAO, ClienteDAO clienteDAO,
                         EmpleadoDAO empleadoDAO, VentaDAO ventaDAO,
                         InventarioDAO inventarioDAO) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Dashboard");
        titulo.addClassNames("page-title");
        add(titulo);

        try {
            int totalComb = combustibleDAO.listar().size();
            int totalCli = clienteDAO.listar().size();
            int totalEmp = empleadoDAO.listar().size();
            int totalInv = inventarioDAO.listar().size();
            long totalVentas = ventaDAO.listar().size();
            double ventasHoy = ventaDAO.totalVentasPeriodo(
                    LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));

            HorizontalLayout cards = new HorizontalLayout(
                    tarjeta(VaadinIcon.DROP, "Combustibles", String.valueOf(totalComb), "#1565c0"),
                    tarjeta(VaadinIcon.USER, "Clientes", String.valueOf(totalCli), "#2e7d32"),
                    tarjeta(VaadinIcon.GROUP, "Empleados", String.valueOf(totalEmp), "#e65100"),
                    tarjeta(VaadinIcon.CASH, "Ventas hoy", String.format("$%,.2f", ventasHoy), "#00838f"),
                    tarjeta(VaadinIcon.ARCHIVE, "Productos", String.valueOf(totalInv), "#4a148c")
            );
            cards.setWidthFull();
            cards.setFlexGrow(1, cards.getComponentAt(0), cards.getComponentAt(1),
                    cards.getComponentAt(2), cards.getComponentAt(3), cards.getComponentAt(4));
            add(cards);

        } catch (Exception e) {
            add(new Paragraph("Error al cargar datos: " + e.getMessage()));
        }
    }

    private VerticalLayout tarjeta(VaadinIcon icono, String titulo, String valor, String color) {
        VerticalLayout v = new VerticalLayout();
        v.addClassNames("dashboard-card");
        v.setWidthFull();
        v.setAlignItems(FlexComponent.Alignment.CENTER);
        v.setSpacing(false);

        Icon icon = new Icon(icono);
        icon.setSize("36px");
        icon.setColor(color);

        Paragraph label = new Paragraph(titulo);
        label.getStyle().set("font-size", "14px").set("color", "#78909c").set("margin", "4px 0");

        Paragraph val = new Paragraph(valor);
        val.getStyle().set("font-size", "28px").set("font-weight", "bold").set("color", color).set("margin", "0");

        v.add(icon, val, label);
        return v;
    }
}
