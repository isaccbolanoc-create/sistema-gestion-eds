package com.gasstation.web.views;

import com.gasstation.web.dao.*;
import com.gasstation.web.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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

@Route(value = "reportes", layout = MainLayout.class)
public class ReportesView extends VerticalLayout {

    @Autowired
    public ReportesView(CombustibleDAO combustibleDAO, ClienteDAO clienteDAO,
                        EmpleadoDAO empleadoDAO, VentaDAO ventaDAO,
                        InventarioDAO inventarioDAO) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Reportes");
        titulo.addClassNames("page-title");
        add(titulo);

        try {
            double ventasHoy = ventaDAO.totalVentasPeriodo(
                    LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));
            int totalComb = combustibleDAO.listar().size();
            int totalCli = clienteDAO.listar().size();
            int totalEmp = empleadoDAO.listar().size();
            double totalInventario = 0;
            for (Inventario inv : inventarioDAO.listar()) {
                Combustible c = combustibleDAO.buscarPorId(inv.getCombustibleId());
                totalInventario += inv.getCantidad() * (c != null ? c.getPrecioCompra() : 0);
            }

            H3 resumenTitle = new H3("Resumen General");
            resumenTitle.addClassNames("section-title");

            HorizontalLayout cards = new HorizontalLayout(
                    tarjeta(VaadinIcon.DROP, "Combustibles", String.valueOf(totalComb), "#1565c0"),
                    tarjeta(VaadinIcon.USER, "Clientes registrados", String.valueOf(totalCli), "#2e7d32"),
                    tarjeta(VaadinIcon.GROUP, "Empleados", String.valueOf(totalEmp), "#e65100"),
                    tarjeta(VaadinIcon.TRENDING_UP, "Ventas hoy", String.format("$%,.2f", ventasHoy), "#00838f"),
                    tarjeta(VaadinIcon.MONEY, "Valor inventario", String.format("$%,.2f", totalInventario), "#4a148c")
            );
            cards.setWidthFull();
            cards.setFlexGrow(1, cards.getComponentAt(0), cards.getComponentAt(1),
                    cards.getComponentAt(2), cards.getComponentAt(3), cards.getComponentAt(4));

            H3 stockTitle = new H3("Stock Actual por Producto");
            stockTitle.addClassNames("section-title");

            Grid<Inventario> gridStock = new Grid<>(Inventario.class, false);
            gridStock.addColumn(inv -> {
                try {
                    Combustible c = combustibleDAO.buscarPorId(inv.getCombustibleId());
                    return c != null ? c.getNombre() : "ID:" + inv.getCombustibleId();
                } catch (Exception e) { return "ID:" + inv.getCombustibleId(); }
            }).setHeader("Combustible").setAutoWidth(true);
            gridStock.addColumn(inv -> {
                try {
                    Combustible c = combustibleDAO.buscarPorId(inv.getCombustibleId());
                    return String.format("%,.2f %s", inv.getCantidad(), c != null ? c.getUnidad() : "");
                } catch (Exception e) { return String.valueOf(inv.getCantidad()); }
            }).setHeader("Stock").setAutoWidth(true);
            gridStock.addColumn(inv -> {
                try {
                    Combustible c = combustibleDAO.buscarPorId(inv.getCombustibleId());
                    double valorVenta = inv.getCantidad() * (c != null ? c.getPrecioVenta() : 0);
                    double valorCompra = inv.getCantidad() * (c != null ? c.getPrecioCompra() : 0);
                    return String.format("Compra: $%,.2f | Venta: $%,.2f", valorCompra, valorVenta);
                } catch (Exception e) { return "$0"; }
            }).setHeader("Valor total").setAutoWidth(true);
            gridStock.setWidthFull();

            try { gridStock.setItems(inventarioDAO.listar()); } catch (Exception ignored) {}

            VerticalLayout stockCard = new VerticalLayout(stockTitle, gridStock);
            stockCard.addClassNames("form-card");

            add(resumenTitle, cards, stockCard);

        } catch (Exception e) {
            add(new Paragraph("Error al cargar reportes: " + e.getMessage()));
        }
    }

    private VerticalLayout tarjeta(VaadinIcon icono, String titulo, String valor, String color) {
        VerticalLayout v = new VerticalLayout();
        v.addClassNames("dashboard-card");
        v.setWidthFull();
        v.setAlignItems(FlexComponent.Alignment.CENTER);
        v.setSpacing(false);

        Icon icon = new Icon(icono);
        icon.setSize("28px");
        icon.setColor(color);

        Paragraph val = new Paragraph(valor);
        val.getStyle().set("font-size", "24px").set("font-weight", "bold").set("color", color).set("margin", "4px 0");

        Paragraph label = new Paragraph(titulo);
        label.getStyle().set("font-size", "13px").set("color", "#78909c").set("margin", "0");

        v.add(icon, val, label);
        return v;
    }
}
