package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.User;
import com.SpringBootMVC.ExpensesTracker.service.ClientService;
import com.SpringBootMVC.ExpensesTracker.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class CustomeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomeAuthenticationSuccessHandler.class);

    UserService userService;
    ClientService clientService;

    @Autowired
    public CustomeAuthenticationSuccessHandler(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userService.findUserByUserName(username);
        if (user == null) {
            log.warn("Login succeeded but no User row found for username {}", username);
            response.sendRedirect(request.getContextPath() + "/showLoginPage");
            return;
        }

        Client client = user.getClient();
        if (client == null) {
            log.warn("User {} has no linked client. Falling back to lookup by user id {}", username, user.getId());
            client = clientService.findClientById(user.getId());
        }

        if (client == null) {
            log.warn("Unable to resolve client for username {}", username);
            response.sendRedirect(request.getContextPath() + "/showLoginPage");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("client", client);
        log.debug("Authenticated username {} mapped to client {}", username, client.getId());
        response.sendRedirect(request.getContextPath() + "/list");
    }
}
