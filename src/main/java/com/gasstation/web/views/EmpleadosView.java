package com.gasstation.web.views;

import com.gasstation.web.dao.EmpleadoDAO;
import com.gasstation.web.model.Empleado;
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

@Route(value = "empleados", layout = MainLayout.class)
public class EmpleadosView extends VerticalLayout {

    @Autowired
    public EmpleadosView(EmpleadoDAO dao) {
        addClassNames("content-section");
        setSpacing(true);

        H2 titulo = new H2("Empleados");
        titulo.addClassNames("page-title");
        add(titulo);

        Grid<Empleado> grid = new Grid<>(Empleado.class, false);
        grid.addColumn(Empleado::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Empleado::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(e -> e.getEmail() != null ? e.getEmail() : "-").setHeader("Email").setAutoWidth(true);
        grid.addColumn(Empleado::getCargo).setHeader("Cargo").setAutoWidth(true);
        grid.addColumn(e -> String.format("$%,.2f", e.getSalario())).setHeader("Salario").setAutoWidth(true);
        grid.addColumn(e -> e.getHorario() != null ? e.getHorario() : "-").setHeader("Horario").setAutoWidth(true);
        grid.setWidthFull();

        TextField nombre = new TextField("Nombre");
        TextField telefono = new TextField("Teléfono");
        TextField email = new TextField("Email");
        TextField cargo = new TextField("Cargo");
        TextField salario = new TextField("Salario");
        TextField horario = new TextField("Horario");

        Button agregar = new Button("Guardar");
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button eliminar = new Button("Eliminar");
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button limpiarBtn = new Button("Limpiar");

        agregar.addClickListener(e -> {
            try {
                Empleado emp = new Empleado();
                emp.setNombre(nombre.getValue());
                emp.setTelefono(telefono.getValue());
                emp.setEmail(email.getValue());
                emp.setCargo(cargo.getValue());
                emp.setSalario(Double.parseDouble(salario.getValue().isEmpty() ? "0" : salario.getValue()));
                emp.setHorario(horario.getValue());
                dao.insertar(emp);
                grid.setItems(dao.listar());
                limpiarCampos(nombre, telefono, email, cargo, salario, horario);
                Notification.show("Empleado agregado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        eliminar.addClickListener(e -> {
            Empleado sel = grid.asSingleSelect().getValue();
            if (sel != null) try {
                dao.eliminar(sel.getId());
                grid.setItems(dao.listar());
                limpiarCampos(nombre, telefono, email, cargo, salario, horario);
                Notification.show("Empleado eliminado", 3000, Notification.Position.BOTTOM_CENTER);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        limpiarBtn.addClickListener(e -> limpiarCampos(nombre, telefono, email, cargo, salario, horario));

        grid.asSingleSelect().addValueChangeListener(e -> {
            Empleado emp = e.getValue();
            if (emp != null) {
                nombre.setValue(emp.getNombre());
                telefono.setValue(emp.getTelefono() != null ? emp.getTelefono() : "");
                email.setValue(emp.getEmail() != null ? emp.getEmail() : "");
                cargo.setValue(emp.getCargo());
                salario.setValue(String.valueOf(emp.getSalario()));
                horario.setValue(emp.getHorario() != null ? emp.getHorario() : "");
            }
        });

        try { grid.setItems(dao.listar()); } catch (Exception ignored) {}

        VerticalLayout form = new VerticalLayout(
                new HorizontalLayout(nombre, telefono, email),
                new HorizontalLayout(cargo, salario, horario),
                new HorizontalLayout(agregar, eliminar, limpiarBtn));
        form.addClassNames("form-card");
        form.setSpacing(true);

        add(form, grid);
    }

    private void limpiarCampos(TextField... campos) {
        for (TextField c : campos) c.clear();
    }
}
