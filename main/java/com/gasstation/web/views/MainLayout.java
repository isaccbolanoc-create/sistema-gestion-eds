package com.gasstation.web.views;

import com.gasstation.web.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final AuthService authService;

    public MainLayout(AuthService authService) {
        this.authService = authService;

        DrawerToggle toggle = new DrawerToggle();

        H1 titulo = new H1("PetroCure");
        titulo.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        Button logout = new Button("Cerrar sesión", new Icon(VaadinIcon.SIGN_OUT));
        logout.addClickListener(e -> {
            authService.logout();
            UI.getCurrent().navigate("login");
        });
        logout.getStyle().set("margin-left", "auto").set("background", "transparent").set("color", "#bbdefb");

        HorizontalLayout header = new HorizontalLayout(toggle, titulo, logout);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, new Icon(VaadinIcon.DASHBOARD)));
        nav.addItem(new SideNavItem("Combustibles", CombustiblesView.class, new Icon(VaadinIcon.DROP)));
        nav.addItem(new SideNavItem("Clientes", ClientesView.class, new Icon(VaadinIcon.USER)));
        nav.addItem(new SideNavItem("Empleados", EmpleadosView.class, new Icon(VaadinIcon.GROUP)));
        nav.addItem(new SideNavItem("Ventas", VentasView.class, new Icon(VaadinIcon.CASH)));
        nav.addItem(new SideNavItem("Inventario", InventarioView.class, new Icon(VaadinIcon.ARCHIVE)));
        nav.addItem(new SideNavItem("Reportes", ReportesView.class, new Icon(VaadinIcon.CHART)));

        addToDrawer(new Scroller(nav));
        addToNavbar(header);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!authService.isAuthenticated()) {
            event.rerouteTo("login");
        }
    }
}
