package com.gasstation.web.views;

import com.gasstation.web.dao.CombustibleDAO;
import com.gasstation.web.model.Combustible;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "combustibles", layout = MainLayout.class)
public class CombustiblesView extends VerticalLayout {

    @Autowired
    public CombustiblesView(CombustibleDAO dao) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Combustibles");
        titulo.addClassNames("page-title");
        add(titulo);

        Grid<Combustible> grid = new Grid<>(Combustible.class, false);
        grid.addColumn(Combustible::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Combustible::getTipo).setHeader("Tipo").setAutoWidth(true);
        grid.addColumn(c -> String.format("$%,.2f", c.getPrecioVenta())).setHeader("Precio venta").setAutoWidth(true);
        grid.addColumn(c -> String.format("$%,.2f", c.getPrecioCompra())).setHeader("Precio compra").setAutoWidth(true);
        grid.addColumn(Combustible::getUnidad).setHeader("Unidad").setAutoWidth(true);
        grid.setWidthFull();

        TextField nombre = new TextField("Nombre");
        TextField tipo = new TextField("Tipo");
        TextField precioVenta = new TextField("Precio venta");
        TextField precioCompra = new TextField("Precio compra");
        TextField unidad = new TextField("Unidad");

        Button agregar = new Button("Guardar");
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button eliminar = new Button("Eliminar");
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button limpiarBtn = new Button("Limpiar");

        agregar.addClickListener(e -> {
            try {
                Combustible c = new Combustible();
                c.setNombre(nombre.getValue());
                c.setTipo(tipo.getValue());
                c.setPrecioVenta(Double.parseDouble(precioVenta.getValue()));
                c.setPrecioCompra(Double.parseDouble(precioCompra.getValue()));
                c.setUnidad(unidad.getValue());
                dao.insertar(c);
                grid.setItems(dao.listar());
                limpiarCampos(nombre, tipo, precioVenta, precioCompra, unidad);
                Notification.show("Combustible agregado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        eliminar.addClickListener(e -> {
            Combustible sel = grid.asSingleSelect().getValue();
            if (sel != null) try {
                dao.eliminar(sel.getId());
                grid.setItems(dao.listar());
                limpiarCampos(nombre, tipo, precioVenta, precioCompra, unidad);
                Notification.show("Combustible eliminado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        limpiarBtn.addClickListener(e -> limpiarCampos(nombre, tipo, precioVenta, precioCompra, unidad));

        grid.asSingleSelect().addValueChangeListener(e -> {
            Combustible c = e.getValue();
            if (c != null) {
                nombre.setValue(c.getNombre());
                tipo.setValue(c.getTipo());
                precioVenta.setValue(String.valueOf(c.getPrecioVenta()));
                precioCompra.setValue(String.valueOf(c.getPrecioCompra()));
                unidad.setValue(c.getUnidad());
            }
        });

        try { grid.setItems(dao.listar()); } catch (Exception ignored) {}

        VerticalLayout form = new VerticalLayout(
                new HorizontalLayout(nombre, tipo, unidad),
                new HorizontalLayout(precioVenta, precioCompra),
                new HorizontalLayout(agregar, eliminar, limpiarBtn));
        form.addClassNames("form-card");
        form.setSpacing(true);

        add(form, grid);
    }

    private void limpiarCampos(TextField... campos) {
        for (TextField c : campos) c.clear();
    }
}
