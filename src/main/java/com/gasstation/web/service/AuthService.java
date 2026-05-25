package com.gasstation.web.service;

import com.gasstation.web.dao.UsuarioDAO;
import com.gasstation.web.model.Usuario;
import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario login(String username, String password) {
        try {
            if (usuarioDAO.validar(username, password)) {
                Usuario u = usuarioDAO.buscarPorUsername(username);
                UI.getCurrent().getSession().setAttribute("user", u);
                return u;
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public Usuario getUser() {
        return (Usuario) UI.getCurrent().getSession().getAttribute("user");
    }

    public boolean isAuthenticated() {
        return getUser() != null;
    }

    public void logout() {
        UI.getCurrent().getSession().setAttribute("user", null);
    }
}
