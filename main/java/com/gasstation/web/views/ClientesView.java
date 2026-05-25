package com.gasstation.web.views;

import com.gasstation.web.dao.ClienteDAO;
import com.gasstation.web.model.Cliente;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "clientes", layout = MainLayout.class)
public class ClientesView extends VerticalLayout {

    @Autowired
    public ClientesView(ClienteDAO dao) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Clientes");
        titulo.addClassNames("page-title");
        add(titulo);

        Grid<Cliente> grid = new Grid<>(Cliente.class, false);
        grid.addColumn(Cliente::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Cliente::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Cliente::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Cliente::getDireccion).setHeader("Dirección").setAutoWidth(true);
        grid.addColumn(c -> String.format("$%,.2f", c.getCreditoDisponible())).setHeader("Crédito disponible").setAutoWidth(true);
        grid.setWidthFull();

        TextField nombre = new TextField("Nombre");
        TextField telefono = new TextField("Teléfono");
        TextField email = new TextField("Email");
        TextField direccion = new TextField("Dirección");
        TextField credito = new TextField("Crédito disponible");

        Button agregar = new Button("Guardar");
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button eliminar = new Button("Eliminar");
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button limpiarBtn = new Button("Limpiar");

        agregar.addClickListener(e -> {
            try {
                Cliente c = new Cliente();
                c.setNombre(nombre.getValue());
                c.setTelefono(telefono.getValue());
                c.setEmail(email.getValue());
                c.setDireccion(direccion.getValue());
                c.setCreditoDisponible(Double.parseDouble(credito.getValue().isEmpty() ? "0" : credito.getValue()));
                dao.insertar(c);
                grid.setItems(dao.listar());
                limpiarCampos(nombre, telefono, email, direccion, credito);
                Notification.show("Cliente agregado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        eliminar.addClickListener(e -> {
            Cliente sel = grid.asSingleSelect().getValue();
            if (sel != null) try {
                dao.eliminar(sel.getId());
                grid.setItems(dao.listar());
                limpiarCampos(nombre, telefono, email, direccion, credito);
                Notification.show("Cliente eliminado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        limpiarBtn.addClickListener(e -> limpiarCampos(nombre, telefono, email, direccion, credito));

        grid.asSingleSelect().addValueChangeListener(e -> {
            Cliente c = e.getValue();
            if (c != null) {
                nombre.setValue(c.getNombre());
                telefono.setValue(c.getTelefono() != null ? c.getTelefono() : "");
                email.setValue(c.getEmail() != null ? c.getEmail() : "");
                direccion.setValue(c.getDireccion() != null ? c.getDireccion() : "");
                credito.setValue(String.valueOf(c.getCreditoDisponible()));
            }
        });

        try { grid.setItems(dao.listar()); } catch (Exception ignored) {}

        VerticalLayout form = new VerticalLayout(
                new HorizontalLayout(nombre, telefono, email),
                new HorizontalLayout(direccion, credito),
                new HorizontalLayout(agregar, eliminar, limpiarBtn));
        form.addClassNames("form-card");
        form.setSpacing(true);

        add(form, grid);
    }

    private void limpiarCampos(TextField... campos) {
        for (TextField c : campos) c.clear();
    }
}
