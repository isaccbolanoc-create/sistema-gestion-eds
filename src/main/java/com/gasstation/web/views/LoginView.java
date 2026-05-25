package com.gasstation.web.views;

import com.gasstation.web.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView(AuthService authService) {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #1a237e 0%, #283593 100%)");

        VerticalLayout card = new VerticalLayout();
        card.addClassNames("form-card");
        card.setWidth("380px");
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(true);

        H1 titulo = new H1("PetroCure");
        titulo.getStyle().set("color", "#1a237e").set("margin-bottom", "4px");

        Paragraph subt = new Paragraph("Iniciar sesión");
        subt.getStyle().set("color", "#78909c").set("margin", "0 0 16px 0");

        TextField username = new TextField("Usuario");
        username.setWidthFull();
        username.focus();

        PasswordField password = new PasswordField("Contraseña");
        password.setWidthFull();

        Button login = new Button("Ingresar");
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.setWidthFull();

        login.addClickListener(e -> {
            String u = username.getValue();
            String p = password.getValue();
            if (u.isEmpty() || p.isEmpty()) {
                Notification.show("Complete todos los campos", 3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            if (authService.login(u, p) != null) {
                UI.getCurrent().navigate("");
            } else {
                Notification.show("Usuario o contraseña incorrectos", 4000, Notification.Position.BOTTOM_CENTER);
                password.clear();
                username.focus();
            }
        });

        password.addKeyPressListener(e -> {
            if (e.getKey().equals("Enter")) login.click();
        });

        card.add(titulo, subt, username, password, login);
        add(card);
    }
}
