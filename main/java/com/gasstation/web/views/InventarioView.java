package com.gasstation.web.views;

import com.gasstation.web.dao.*;
import com.gasstation.web.model.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "inventario", layout = MainLayout.class)
public class InventarioView extends VerticalLayout {

    @Autowired
    public InventarioView(CombustibleDAO combustibleDAO, InventarioDAO inventarioDAO,
                          MovimientoInventarioDAO movimientoDAO) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Inventario");
        titulo.addClassNames("page-title");
        add(titulo);

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
        }).setHeader("Stock actual").setAutoWidth(true);
        gridStock.addColumn(inv -> {
            try {
                Combustible c = combustibleDAO.buscarPorId(inv.getCombustibleId());
                double valor = inv.getCantidad() * (c != null ? c.getPrecioVenta() : 0);
                return String.format("$%,.2f", valor);
            } catch (Exception e) { return "$0"; }
        }).setHeader("Valor venta").setAutoWidth(true);
        gridStock.addColumn(Inventario::getUbicacion).setHeader("Ubicación").setAutoWidth(true);
        gridStock.setWidthFull();

        try { gridStock.setItems(inventarioDAO.listar()); } catch (Exception ignored) {}

        H3 stockTitle = new H3("Stock actual");
        stockTitle.addClassNames("section-title");

        VerticalLayout stockSection = new VerticalLayout(stockTitle, gridStock);
        stockSection.addClassNames("form-card");

        ComboBox<Combustible> comboC = new ComboBox<>("Combustible");
        comboC.setItemLabelGenerator(Combustible::getNombre);
        comboC.setWidthFull();
        try { comboC.setItems(combustibleDAO.listar()); } catch (Exception ignored) {}

        NumberField cantidad = new NumberField("Cantidad");
        cantidad.setMin(0);
        cantidad.setWidthFull();

        TextField motivo = new TextField("Motivo");
        motivo.setWidthFull();

        Button agregar = new Button("Agregar stock");
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        H3 movTitle = new H3("Movimientos de inventario");
        movTitle.addClassNames("section-title");

        Grid<MovimientoInventario> gridMov = new Grid<>(MovimientoInventario.class, false);
        gridMov.addColumn(MovimientoInventario::getId).setHeader("#").setAutoWidth(true);
        gridMov.addColumn(m -> {
            try {
                Combustible c = combustibleDAO.buscarPorId(m.getCombustibleId());
                return c != null ? c.getNombre() : "ID:" + m.getCombustibleId();
            } catch (Exception e) { return "ID:" + m.getCombustibleId(); }
        }).setHeader("Combustible").setAutoWidth(true);
        gridMov.addColumn(MovimientoInventario::getTipo).setHeader("Tipo").setAutoWidth(true);
        gridMov.addColumn(m -> String.format("%,.2f", m.getCantidad())).setHeader("Cantidad").setAutoWidth(true);
        gridMov.addColumn(MovimientoInventario::getMotivo).setHeader("Motivo").setAutoWidth(true);
        gridMov.addColumn(m -> m.getFecha() != null ? m.getFecha().toLocalDate().toString() : "").setHeader("Fecha").setAutoWidth(true);
        gridMov.setWidthFull();

        try { gridMov.setItems(movimientoDAO.listar()); } catch (Exception ignored) {}

        agregar.addClickListener(e -> {
            try {
                Combustible c = comboC.getValue();
                if (c == null) { Notification.show("Seleccione combustible"); return; }
                double cant = cantidad.getValue() != null ? cantidad.getValue() : 0;
                if (cant <= 0) { Notification.show("Cantidad inválida"); return; }

                Inventario inv = inventarioDAO.buscarPorCombustibleId(c.getId());
                if (inv == null) {
                    inv = new Inventario();
                    inv.setCombustibleId(c.getId());
                    inv.setCantidad(cant);
                } else {
                    inventarioDAO.actualizarCantidad(c.getId(), inv.getCantidad() + cant);
                }
                MovimientoInventario mov = new MovimientoInventario();
                mov.setCombustibleId(c.getId());
                mov.setTipo("entrada");
                mov.setCantidad(cant);
                mov.setMotivo(motivo.getValue());
                mov.setUsuario("web");
                movimientoDAO.insertar(mov);

                Notification.show("Stock actualizado", 3000, Notification.Position.BOTTOM_CENTER);
                gridStock.setItems(inventarioDAO.listar());
                gridMov.setItems(movimientoDAO.listar());
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        VerticalLayout movSection = new VerticalLayout(movTitle, gridMov);
        movSection.addClassNames("form-card");

        HorizontalLayout addRow = new HorizontalLayout(comboC, cantidad, motivo);
        addRow.setWidthFull();

        H3 addTitle = new H3("Agregar stock");
        addTitle.addClassNames("section-title");

        VerticalLayout addForm = new VerticalLayout(addTitle, addRow, agregar);
        addForm.addClassNames("form-card");

        add(stockSection, addForm, movSection);
    }
}
