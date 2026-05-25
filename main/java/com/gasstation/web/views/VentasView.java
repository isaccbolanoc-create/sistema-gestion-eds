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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "ventas", layout = MainLayout.class)
public class VentasView extends VerticalLayout {

    @Autowired
    public VentasView(CombustibleDAO combustibleDAO, ClienteDAO clienteDAO,
                      EmpleadoDAO empleadoDAO, VentaDAO ventaDAO,
                      InventarioDAO inventarioDAO, MovimientoInventarioDAO movimientoDAO) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Ventas");
        titulo.addClassNames("page-title");
        add(titulo);

        Grid<Venta> grid = new Grid<>(Venta.class, false);
        grid.addColumn(Venta::getId).setHeader("#").setAutoWidth(true);
        grid.addColumn(v -> nombreCombustible(combustibleDAO, v.getCombustibleId())).setHeader("Combustible").setAutoWidth(true);
        grid.addColumn(v -> nombreCliente(clienteDAO, v.getClienteId())).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(v -> nombreEmpleado(empleadoDAO, v.getEmpleadoId())).setHeader("Empleado").setAutoWidth(true);
        grid.addColumn(v -> String.format("%,.2f L", v.getCantidad())).setHeader("Cantidad").setAutoWidth(true);
        grid.addColumn(v -> String.format("$%,.2f", v.getTotal())).setHeader("Total").setAutoWidth(true);
        grid.addColumn(Venta::getTipoPago).setHeader("Pago").setAutoWidth(true);
        grid.addColumn(v -> v.getFecha() != null ? v.getFecha().toLocalDate().toString() : "").setHeader("Fecha").setAutoWidth(true);
        grid.setWidthFull();

        ComboBox<Combustible> comboC = new ComboBox<>("Combustible");
        comboC.setItemLabelGenerator(c -> c.getNombre() + " - $" + String.format("%,.0f", c.getPrecioVenta()) + "/" + c.getUnidad());
        comboC.setWidthFull();
        try { comboC.setItems(combustibleDAO.listar()); } catch (Exception ignored) {}

        ComboBox<Cliente> comboCl = new ComboBox<>("Cliente (opcional)");
        comboCl.setItemLabelGenerator(Cliente::getNombre);
        comboCl.setWidthFull();
        try { comboCl.setItems(clienteDAO.listar()); } catch (Exception ignored) {}

        ComboBox<Empleado> comboE = new ComboBox<>("Empleado");
        comboE.setItemLabelGenerator(Empleado::getNombre);
        comboE.setWidthFull();
        try { comboE.setItems(empleadoDAO.listar()); } catch (Exception ignored) {}

        NumberField cantidad = new NumberField("Cantidad (litros)");
        cantidad.setMin(0);
        cantidad.setWidthFull();

        Select<String> tipoPago = new Select<>();
        tipoPago.setLabel("Tipo de pago");
        tipoPago.setItems("efectivo", "tarjeta", "transferencia", "crédito");
        tipoPago.setValue("efectivo");
        tipoPago.setWidthFull();

        Button registrar = new Button("Registrar venta");
        registrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        registrar.addClickListener(e -> {
            try {
                Combustible c = comboC.getValue();
                if (c == null) { Notification.show("Seleccione un combustible"); return; }
                Empleado emp = comboE.getValue();
                if (emp == null) { Notification.show("Seleccione un empleado"); return; }
                double cant = cantidad.getValue() != null ? cantidad.getValue() : 0;
                if (cant <= 0) { Notification.show("Cantidad inválida"); return; }

                Inventario inv = inventarioDAO.buscarPorCombustibleId(c.getId());
                if (inv != null && cant > inv.getCantidad()) {
                    Notification.show("Stock insuficiente. Disponible: " + String.format("%,.2f", inv.getCantidad()) + " " + c.getUnidad());
                    return;
                }

                double total = cant * c.getPrecioVenta();
                Venta v = new Venta();
                v.setCombustibleId(c.getId());
                v.setClienteId(comboCl.getValue() != null ? comboCl.getValue().getId() : null);
                v.setEmpleadoId(emp.getId());
                v.setCantidad(cant);
                v.setPrecioUnitario(c.getPrecioVenta());
                v.setTotal(total);
                v.setTipoPago(tipoPago.getValue());
                ventaDAO.insertar(v);

                if (inv != null) {
                    inventarioDAO.actualizarCantidad(c.getId(), inv.getCantidad() - cant);
                    MovimientoInventario mov = new MovimientoInventario();
                    mov.setCombustibleId(c.getId());
                    mov.setTipo("salida");
                    mov.setCantidad(cant);
                    mov.setMotivo("Venta #" + v.getId());
                    mov.setUsuario("web");
                    movimientoDAO.insertar(mov);
                }

                Notification.show("Venta #" + v.getId() + " registrada: $" + String.format("%,.2f", total), 5000, Notification.Position.BOTTOM_CENTER);
                grid.setItems(ventaDAO.listar());
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        try { grid.setItems(ventaDAO.listar()); } catch (Exception ignored) {}

        H3 formTitle = new H3("Registrar nueva venta");
        VerticalLayout form = new VerticalLayout();
        form.addClassNames("form-card");
        form.setSpacing(true);
        form.add(formTitle);

        HorizontalLayout row1 = new HorizontalLayout(comboC, comboCl, comboE);
        row1.setWidthFull();
        HorizontalLayout row2 = new HorizontalLayout(cantidad, tipoPago);
        row2.setWidthFull();
        form.add(row1, row2, registrar);

        H3 historialTitle = new H3("Historial de ventas");
        historialTitle.addClassNames("section-title");

        add(form, historialTitle, grid);
    }

    private String nombreCombustible(CombustibleDAO dao, int id) {
        try {
            Combustible c = dao.buscarPorId(id);
            return c != null ? c.getNombre() : "ID:" + id;
        } catch (Exception e) { return "ID:" + id; }
    }

    private String nombreCliente(ClienteDAO dao, Integer id) {
        if (id == null) return "-";
        try {
            Cliente c = dao.buscarPorId(id);
            return c != null ? c.getNombre() : "ID:" + id;
        } catch (Exception e) { return "ID:" + id; }
    }

    private String nombreEmpleado(EmpleadoDAO dao, int id) {
        try {
            Empleado e = dao.buscarPorId(id);
            return e != null ? e.getNombre() : "ID:" + id;
        } catch (Exception ex) { return "ID:" + id; }
    }
}
