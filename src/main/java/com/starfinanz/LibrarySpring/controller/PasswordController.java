package com.starfinanz.LibrarySpring.controller;

import com.starfinanz.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    private final LibraryService libraryService;

    public PasswordController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @GetMapping("/changePassword/{userId}")
    public String loginPage(
            @PathVariable int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null) {
            return "redirect:/";
        }

        var user = libraryService.findUserID(userId);

        model.addAttribute("user", user);

        return "change-password";
    }



    @PostMapping("/changePassword/{userId}")
    public String changePassword (
            @PathVariable int userId,
            @RequestParam String newPassword,
            @RequestParam String oldPassword,
            @RequestParam String newPasswordCheck,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null) {
            return "redirect:/";
        }

        var user = libraryService.findUserID(userId);
        model.addAttribute("user", user);

        if (!newPassword.equals(newPasswordCheck)) {
            model.addAttribute("error", "Passwörter müssen übereinstimmen.");
            return "change-password";
        }

        if (libraryService.replacePassword(userId, newPassword, oldPassword)) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Passwort konnte nicht geändert werden");
            return "error-page";
        }
    }
}