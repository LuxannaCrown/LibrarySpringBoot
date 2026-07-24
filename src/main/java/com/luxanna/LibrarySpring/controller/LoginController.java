package com.luxanna.LibrarySpring.controller;

import com.luxanna.LibrarySpring.model.User;
import com.luxanna.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final LibraryService libraryService;

    public LoginController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @GetMapping("/")
    public String loginPage() {
        return "login";
    }



    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = libraryService.findUserName(username.trim());


        if (username.equals("Admin")) {
            if(libraryService.checkLogin(username.trim(), password)) {
                session.setAttribute("loggedInAdmin",  true);
                session.setAttribute("loggedUser", user.getId());  //Setzt session-token als Admin login
                return"redirect:/admin";
            }
        }

        if (libraryService.checkLogin(username.trim(), password)) {
            session.setAttribute("loggedIn", true);
            session.setAttribute("loggedUser", user.getId());  //Setzt session-token als User login
            return "redirect:/home";
        }

        model.addAttribute("error",
                "Benutzername oder Passwort falsch.");

        return "login";
    }
}