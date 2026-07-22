package com.starfinanz.LibrarySpring.controller;

import com.starfinanz.LibrarySpring.service.LibraryService;
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

        if (username.equals("Admin")) {
            if(libraryService.checkLogin(username, password)) {
                session.setAttribute("loggedInAdmin",  true);
                return"redirect:/admin";
            }
        }


        if (libraryService.checkLogin(username, password)) {
            session.setAttribute("loggedIn", true);
            return "redirect:/home";
        }

        model.addAttribute("error",
                "Benutzername oder Passwort falsch.");

        return "login";
    }
}